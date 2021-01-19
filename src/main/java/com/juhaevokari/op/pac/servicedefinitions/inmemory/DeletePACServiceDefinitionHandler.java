package com.juhaevokari.op.pac.servicedefinitions.inmemory;

import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinition;
import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinitionRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DeletePACServiceDefinitionHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(PACServiceDefinitionRestApi.class);
  private final HashMap<UUID, PACServiceDefinition> serviceDefinition;

  public DeletePACServiceDefinitionHandler(HashMap<UUID, PACServiceDefinition> serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
  }

  @Override
  public void handle(RoutingContext context) {
    String accountId = PACServiceDefinitionRestApi.getServiceDefinitionId(context);
    final PACServiceDefinition deleted = serviceDefinition.remove(UUID.fromString(accountId));
    LOG.info("Deleted: {}, Remaining: {}", deleted, serviceDefinition.values());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
      .end(deleted.toJsonObject().toBuffer());
  }
}
