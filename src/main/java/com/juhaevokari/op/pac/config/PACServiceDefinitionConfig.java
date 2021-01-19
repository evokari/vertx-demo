package com.juhaevokari.op.pac.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class PACServiceDefinitionConfig {

  int serverPort;
  String version;
  DbConfig dbConfig;

  public static PACServiceDefinitionConfig from(final JsonObject config) {
    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
    if(Objects.isNull(serverPort)) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured.");
    }
    final String version = config.getString("version");
    if(Objects.isNull(version)) {
      throw new RuntimeException("version is not configured in yaml.");
    }

    return PACServiceDefinitionConfig.builder()
      .serverPort(config.getInteger(ConfigLoader.SERVER_PORT))
      .version(version)
      .dbConfig(parseDbConfig(config))
      .build();
  }

  private static DbConfig parseDbConfig(JsonObject config) {
    return DbConfig.builder()
      .host(config.getString(ConfigLoader.DB_HOST))
      .port(config.getInteger(ConfigLoader.DB_PORT))
      .database(config.getString(ConfigLoader.DB_DATABASE))
      .user(config.getString(ConfigLoader.DB_USER))
      .password(config.getString(ConfigLoader.DB_PASSWORD))
      .build();
  }
}
