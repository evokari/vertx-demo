package com.juhaevokari.op.pac.servicedefinitions.inmemory;

import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinition;
import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinitionRestApi;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class PutPACServiceDefinitionHandler implements Handler<RoutingContext> {

  private final HashMap<UUID, PACServiceDefinition> watchListPerAccount;

  public PutPACServiceDefinitionHandler(HashMap<UUID, PACServiceDefinition> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }
  @Override
  public void handle(RoutingContext context) {
    String serviceDefinitionId = PACServiceDefinitionRestApi.getServiceDefinitionId(context);

    JsonObject json = context.getBodyAsJson();
    var serviceDefinition = json.mapTo(PACServiceDefinition.class);
    watchListPerAccount.put(UUID.fromString(serviceDefinitionId), serviceDefinition);
    context.response().end(json.toBuffer());
  }
}
