package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLDatabaseClient extends DatabaseClient {
  @Override
  public void connect() {

  }

  @Override
  public void disconnect() {

  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return null;
  }

  @Override
  public CompletableFuture<Void> saveUser(@NotNull User user) {
    return null;
  }

  @Override
  public CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts) {
    return null;
  }
}
