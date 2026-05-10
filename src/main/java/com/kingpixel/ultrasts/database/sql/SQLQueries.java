package com.kingpixel.ultrasts.database.sql;

public interface SQLQueries {

  String createTable();

  String createIndex();

  String upsertUser();

  String findUserByUUID();

  String findTopUsers();

}