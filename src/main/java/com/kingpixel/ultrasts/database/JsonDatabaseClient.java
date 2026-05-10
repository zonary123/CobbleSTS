package com.kingpixel.ultrasts.database;

import com.kingpixel.cobbleutils.util.UtilsFile;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JsonDatabaseClient extends DatabaseClient {
  private static Path PATH;

  @Override
  public void connect() {
    PATH = UltraSTS.getPath().resolve("users");
    PATH.toFile().mkdirs();
  }

  @Override
  public void disconnect() {
    saveAll().join();
  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return UltraSTS.getAsyncContext().supply(() -> {
      User user = getUser(uuid);
      if (user != null) return user;
      Path path = PATH.resolve(uuid + ".json");
      try {
        return UtilsFile.read(path, User.class);
      } catch (Exception e) {
        UltraSTS.LOGGER.error("Failed to find user: " + e.getMessage());
        return null;
      }
    });
  }

  @Override
  public CompletableFuture<Void> saveUser(@NotNull User user) {
    return UltraSTS.getAsyncContext().runAsync(() -> {
      Path path = PATH.resolve(user.getUuid() + ".json");
      try {
        UtilsFile.write(path, user);
      } catch (Exception e) {
        UltraSTS.LOGGER.error("Failed to save user: " + e.getMessage());
      }
    });
  }

  @Override
  public CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts) {
    int safeLimit = Math.max(1, limit);
    int safePage = Math.max(1, page);
    long skip = (long) (safePage - 1) * safeLimit;

    try {
      var files = UtilsFile.getAllJsonFiles(PATH);

      return UltraSTS.getAsyncContext().supply(() ->
        files.stream()
          .map(path -> {
            try {
              return UtilsFile.read(path, User.class);
            } catch (Exception e) {
              UltraSTS.LOGGER.error("Failed to read user file: " + e.getMessage());
              return null;
            }
          })
          .filter(user -> user != null &&
            user.getMoneyGained().containsKey(sts.getId()))
          .sorted((u1, u2) -> {
            BigDecimal money1 = u1.getMoneyGained()
              .getOrDefault(sts.getId(), BigDecimal.ZERO);
            BigDecimal money2 = u2.getMoneyGained()
              .getOrDefault(sts.getId(), BigDecimal.ZERO);
            return money2.compareTo(money1);
          })
          .skip(skip)
          .limit(safeLimit)
          .toList()
      );

    } catch (Exception e) {
      e.printStackTrace();
      return CompletableFuture.completedFuture(List.of());
    }
  }

  @Override
  public CompletableFuture<Void> saveAll() {
    var users = getSavableUsers();
    if (users.isEmpty()) return CompletableFuture.completedFuture(null);

    return CompletableFuture.allOf(
      users.stream()
        .map(user -> UltraSTS.getAsyncContext().runAsync(() -> {
          Path path = PATH.resolve(user.getUuid() + ".json");
          try {
            UtilsFile.write(path, user);
            user.setDirty(false);
          } catch (Exception e) {
            UltraSTS.LOGGER.error("Failed to save user: " + e.getMessage());
          }
        }))
        .toArray(CompletableFuture[]::new)
    );
  }
}
