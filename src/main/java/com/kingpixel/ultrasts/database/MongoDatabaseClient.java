package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDatabaseClient extends DatabaseClient {
  private MongoClient client;
  private MongoDatabase database;
  private MongoCollection<Document> usersCollection;

  @Override
  public void connect() {
    var config = UltraSTS.config.getDatabase();
    var settings = MongoClientSettings.builder()
      .applicationName(UltraSTS.MOD_NAME)
      .applyConnectionString(new ConnectionString(config.getUrl()))
      .build();

    client = MongoClients.create(settings);
    database = client.getDatabase(config.getDatabase());
    usersCollection = database.getCollection("users");
  }

  @Override
  public void disconnect() {
    saveAll().join();
    if (client != null) client.close();
  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return UltraSTS.ASYNC.supply(() -> {
      User user = getUser(uuid);
      if (user != null) return user;

      Document doc = usersCollection.find(new Document("uuid", uuid.toString())).first();
      if (doc == null) return null;

      try {
        return User.fromDocument(doc);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    });
  }

  @Override
  public CompletableFuture<Void> saveUser(@NotNull User user) {
    return UltraSTS.ASYNC.runAsync(() -> {
      Document doc = user.toDocument();
      usersCollection.replaceOne(new Document("uuid", user.getUuid().toString()), doc, new ReplaceOptions().upsert(true));
    });
  }

  @Override
  public CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts) {
    return UltraSTS.ASYNC.supply(() -> usersCollection.find()
      .sort(new Document("moneyGained." + sts.getId(), -1))
      .skip((page - 1) * limit)
      .limit(limit)
      .map(User::fromDocument)
      .into(new ArrayList<>()));
  }
}
