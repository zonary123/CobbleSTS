package com.kingpixel.ultrasts.database;

import com.kingpixel.cobbleutils.util.mongodb.MongoDBManager;
import com.kingpixel.cobbleutils.util.mongodb.MongoDBService;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDatabaseClient extends DatabaseClient {
  /**
   * Dynamically retrieves the users collection from the active MongoDBManager.
   * Prevents stale collection references if the connection pool is cycled or reconnected.
   */
  private MongoCollection<Document> getUsersCollection() {
    return getMongoDBManager().getCollection(UltraSTS.config.getDatabase().getDatabase(), "users");
  }

  /*
   * Helper method to get the MongoDBManager instance for the current database configuration.
   */
  private MongoDBManager getMongoDBManager() {
    return MongoDBService.getOrCreateManager(UltraSTS.config.getDatabase());
  }

  @Override
  public void connect() {
    try {
      getUsersCollection().createIndex(new Document("uuid", 1));
    } catch (Exception e) {
      UltraSTS.LOGGER.debug("Could not create uuid index on MongoDB: {}", e.getMessage());
    }
  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return getMongoDBManager().supplyAsync(() -> {
      User user = getUser(uuid);
      if (user != null) return user;

      Document doc = getUsersCollection().find(new Document("uuid", uuid.toString())).first();
      if (doc == null) return null;

      try {
        return User.fromDocument(doc);
      } catch (Exception e) {
        UltraSTS.LOGGER.error("Failed to find user: " + e.getMessage());
        return null;
      }
    });
  }

  @Override
  public CompletableFuture<Void> saveUser(@NotNull User user) {
    return getMongoDBManager().runAsync(() -> {
      try {
        Document doc = user.toDocument();
        getUsersCollection().replaceOne(new Document("uuid", user.getUuid().toString()), doc, new ReplaceOptions().upsert(true));

        // Mark user as clean after successful save
        user.setDirty(false);

        // Invalidate leaderboard cache since this user's data changed
        invalidateAllLeaderboardCache();
      } catch (Exception e) {
        UltraSTS.LOGGER.error("Failed to save mongo user " + user.getUuid(), e);
      }
    });
  }

  @Override
  public CompletableFuture<List<User>> findTopUsersImpl(int limit, int page, STS sts) {
    return getMongoDBManager().supplyAsync(() -> {
      try {
        return getUsersCollection().find()
          .sort(new Document("moneyGained." + sts.getId(), -1))
          .skip((page - 1) * limit)
          .limit(limit)
          .map(doc -> {
            try {
              return User.fromDocument(doc);
            } catch (Exception e) {
              UltraSTS.LOGGER.warn("Failed to parse user document from MongoDB: {}", e.getMessage());
              return null;
            }
          })
          .into(new ArrayList<>())
          .stream()
          .filter(user -> user != null)
          .toList();
      } catch (Exception e) {
        UltraSTS.LOGGER.error("Error querying top users from MongoDB for STS {}", sts.getId(), e);
        return new ArrayList<>();
      }
    });
  }

  @Override
  public CompletableFuture<Void> saveAll() {
    var users = getSavableUsers();
    if (users.isEmpty()) return CompletableFuture.completedFuture(null);

    return getMongoDBManager().runAsync(() -> {
      try {
        var bulkOperations = users.stream()
          .map(user -> new ReplaceOneModel<>(
            new Document("uuid", user.getUuid().toString()),
            user.toDocument(),
            new ReplaceOptions().upsert(true)
          ))
          .toList();

        getUsersCollection().bulkWrite(bulkOperations);

        // Only mark as clean if bulk write succeeds
        users.forEach(user -> user.setDirty(false));

        // Invalidate leaderboard cache after bulk save
        invalidateAllLeaderboardCache();
      } catch (Exception e) {
        UltraSTS.LOGGER.error("Failed to bulk save users into MongoDB", e);
      }
    });
  }
}