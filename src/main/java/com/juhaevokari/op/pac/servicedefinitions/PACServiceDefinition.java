package com.juhaevokari.op.pac.servicedefinitions;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PACServiceDefinition {

  private String id;
  private String name;
  private String description;
  private String status;

  public PACServiceDefinition(String name, String description, String status) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.description = description;
    this.status = status;
  }

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

}
