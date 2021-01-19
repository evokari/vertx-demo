package com.juhaevokari.op.pac.servicedefinitions.inmemory;

import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinition;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class PostPACServiceDefinitionHandler implements Handler<RoutingContext> {
    public PostPACServiceDefinitionHandler(HashMap<UUID, PACServiceDefinition> pacServiceDefinition) {
    }

    @Override
    public void handle(RoutingContext context) {
        String serviceDefinitionId = UUID.randomUUID().toString();

        JsonObject json = context.getBodyAsJson();
        json.put("id", serviceDefinitionId);
        var serviceDefinition = json.mapTo(PACServiceDefinition.class);
        context.response().end(json.toBuffer());
    }
}
