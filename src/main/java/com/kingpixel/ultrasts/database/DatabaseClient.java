package com.kingpixel.ultrasts.database;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DatabaseClient {
  public static final Cache<UUID, User> USERS = Caffeine.newBuilder()
    .build();

  public abstract void connect();

  public abstract void disconnect();

  public @Nullable User getUser(@NotNull ServerPlayerEntity player) {
    return getUser(player.getUuid());
  }

  public @Nullable User getUser(@NotNull UUID uuid) {
    return USERS.getIfPresent(uuid);
  }

  public CompletableFuture<@Nullable User> findUser(@NotNull ServerPlayerEntity player) {
    return findUser(player.getUuid());
  }

  public abstract CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid);

  public abstract CompletableFuture<Void> saveUser(@NotNull User user);

  public abstract CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts);

  public List<User> getSavableUsers() {
    return USERS.asMap().values().stream()
      .filter(User::isDirty)
      .toList();
  }

  public CompletableFuture<Void> saveAll() {
    var savableUsers = getSavableUsers();
    if (savableUsers.isEmpty()) return CompletableFuture.completedFuture(null);

    return CompletableFuture.allOf(
      savableUsers.stream()
        .map(User::save)
        .toArray(CompletableFuture[]::new)
    );
  }

}
