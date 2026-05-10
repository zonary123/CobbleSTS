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
import java.util.concurrent.TimeUnit;

public class MongoDatabaseClient extends DatabaseClient {
  private MongoCollection<Document> usersCollection;

  /*
   * Helper method to get the MongoDBManager instance for the current database configuration.
   */
  private MongoDBManager getMongoDBManager() {
    return MongoDBService.getOrCreateManager(UltraSTS.config.getDatabase());
  }

  @Override
  public void connect() {
    var config = UltraSTS.config.getDatabase();

    usersCollection = getMongoDBManager().getCollection(config.getDatabase(), "users");
  }


  @Override
  public void disconnect() {
    try {
      saveAll().get(30, TimeUnit.SECONDS);
    } catch (Exception e) {
      UltraSTS.LOGGER.error("Failed to save all users on disconnect: " + e.getMessage());
    }
  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return getMongoDBManager().supplyAsync(() -> {
      User user = getUser(uuid);
      if (user != null) return user;

      Document doc = usersCollection.find(new Document("uuid", uuid.toString())).first();
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
      Document doc = user.toDocument();
      usersCollection.replaceOne(new Document("uuid", user.getUuid().toString()), doc, new ReplaceOptions().upsert(true));
    });
  }

  @Override
  public CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts) {
    return getMongoDBManager().supplyAsync(() -> usersCollection.find()
      .sort(new Document("moneyGained." + sts.getId(), -1))
      .skip((page - 1) * limit)
      .limit(limit)
      .map(User::fromDocument)
      .into(new ArrayList<>()));
  }

  @Override
  public CompletableFuture<Void> saveAll() {
    var users = getSavableUsers();
    if (users.isEmpty()) return CompletableFuture.completedFuture(null);

    return getMongoDBManager().runAsync(() -> {
      var bulkOperations = users.stream()
        .map(user -> new ReplaceOneModel<>(
          new Document("uuid", user.getUuid().toString()),
          user.toDocument(),
          new ReplaceOptions().upsert(true)
        ))
        .toList();

      usersCollection.bulkWrite(bulkOperations);

      users.forEach(user -> user.setDirty(false));
    });
  }
}
