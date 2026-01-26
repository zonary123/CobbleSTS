package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.model.UserInfo;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.util.Utils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

/**
 * Clase para manejar la base de datos MongoDB.
 * Autor: Carlos Varas Alonso - 22/02/2025 4:04
 */
public class DataBaseMongoDB extends DataBaseClient {

  private final MongoClient mongoClient;
  private final MongoDatabase database;
  private final MongoCollection<Document> userCollection;

  public DataBaseMongoDB(DataBaseConfig config) {
    super();
    this.mongoClient = MongoClients.create(config.getUrl());
    this.database = mongoClient.getDatabase(config.getDatabase());
    this.userCollection = database.getCollection("users");
  }

  @Override
  public void connect() {
    CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Connecting to MongoDB database");
  }

  @Override
  public void disconnect() {
    CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Disconnecting from MongoDB database");
    mongoClient.close();
  }

  @Override
  public UserInfo getUserInfo(ServerPlayerEntity player) {
    UserInfo userInfo = DataBaseFactory.users.get(player.getUuid());
    if (userInfo != null) return userInfo;
    Document query = new Document("uuid", player.getUuidAsString());
    Document result = userCollection.find(query).first();

    if (result != null) {
      userInfo = Utils.newWithoutSpacingGson().fromJson(result.toJson(), UserInfo.class);
      DataBaseFactory.users.put(player.getUuid(), userInfo);
      return userInfo;
    }

    // Si no existe, crear un nuevo UserInfo
    userInfo = new UserInfo(player);
    updateUserInfo(userInfo);

    return userInfo;
  }

  @Override
  public void updateUserInfo(UserInfo userInfo) {
    if (userInfo == null) return;
    CompletableFuture.runAsync(() -> {
        Document query = new Document("uuid", userInfo.getUuid());
        Document update = Document.parse(Utils.newWithoutSpacingGson().toJson(userInfo));
        userCollection.replaceOne(query, update, new ReplaceOptions().upsert(true));
      }, UltraSTS.EXECUTOR_STS)
      .exceptionally(ex -> {
        CobbleUtils.LOGGER.error("Error when updating the userinfo of " + userInfo.getPlayerName());
        return null;
      });
  }
}