package com.juhaevokari.op.pac.servicedefinitions;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class PACServiceDefinitionRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(PACServiceDefinitionRestApi.class);

  public static void attach(final Router parent, Pool db) {

    final HashMap<UUID, PACServiceDefinition> pacServiceDefinition = new HashMap<UUID, PACServiceDefinition>();
    final String path = "/servicedefinitions/:id";
    parent.get(path).handler(new GetPACServiceDefinitionFromDBHandler(db));
    parent.put(path).handler(new PutPACServiceDefinitionDBHandler(db));
    parent.delete(path).handler(new DeletePACServiceDefinitionDBHandler(db));

    final String pathAll = "/servicedefinitions/";
    parent.get(pathAll).handler(new GetAllPACServiceDefinitionsFromDBHandler(db));
    parent.post(pathAll).handler(new PostPACServiceDefinitionDBHandler(db));
  }

  public static String getServiceDefinitionId(RoutingContext context) {
    var id = context.pathParam("id");
    LOG.debug("{} for account {}", context.normalizedPath(), id);
    return id;
  }
}
