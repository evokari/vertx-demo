package com.juhaevokari.op.pac.servicedefinitions.inmemory;

import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinition;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class GetAllPACServiceDefinitionsHandler implements Handler<RoutingContext> {
    public GetAllPACServiceDefinitionsHandler(HashMap<UUID, PACServiceDefinition> pacServiceDefinition) {
    }

    @Override
    public void handle(RoutingContext context) {

    }
}
