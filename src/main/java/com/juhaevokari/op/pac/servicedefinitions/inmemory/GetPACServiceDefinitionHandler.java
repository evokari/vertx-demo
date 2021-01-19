package com.juhaevokari.op.pac.servicedefinitions.inmemory;

import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinition;
import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinitionRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GetPACServiceDefinitionHandler implements Handler<RoutingContext> {

  private final HashMap<UUID, PACServiceDefinition> serviceDefinition;

  public GetPACServiceDefinitionHandler(HashMap<UUID, PACServiceDefinition> serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
  }
  @Override
  public void handle(RoutingContext context) {
    var serviceDefinitionId = PACServiceDefinitionRestApi.getServiceDefinitionId(context);
    Optional<PACServiceDefinition> maybeServiceDefinition = Optional.ofNullable(this.serviceDefinition.get(UUID.fromString(serviceDefinitionId)));
    if (maybeServiceDefinition.isEmpty()) {
      // artificialSleep(context);
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message", "Service definition for id " + serviceDefinitionId + " not found.")
          .put("path", context.normalizedPath())
          .toBuffer()
        );
      return;
    }
    context.response().end(maybeServiceDefinition.get().toJsonObject().toBuffer());
  }

  /**
   * Only for perf tests
   * @param context
   */
  private void artificialSleep(RoutingContext context) {
    try {
      final int random = ThreadLocalRandom.current().nextInt(100, 300);
      if (random % 2 == 0) {
        Thread.sleep(random);
        context.response()
                .setStatusCode(500)
                .end("Sleeping...");
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
