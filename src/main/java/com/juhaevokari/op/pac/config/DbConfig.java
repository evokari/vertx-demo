package com.juhaevokari.op.pac.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {

  String host;
  int port;
  String database;
  String user;
  String password;

  @Override
  public String toString() {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", user='" + user + '\'' +
      ", password='****" +
      '}';
  }
}
