package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.servicedefinitions.db.*;
import com.juhaevokari.op.pac.servicedefinitions.inmemory.*;
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

    parent.get(path).handler(new GetPACServiceDefinitionHandler(pacServiceDefinition));
    parent.put(path).handler(new PutPACServiceDefinitionHandler(pacServiceDefinition));
    parent.delete(path).handler(new DeletePACServiceDefinitionHandler(pacServiceDefinition));

    final String pathAll = "/servicedefinitions/";
    parent.get(pathAll).handler(new GetAllPACServiceDefinitionsHandler(pacServiceDefinition));
    parent.post(pathAll).handler(new PostPACServiceDefinitionHandler(pacServiceDefinition));

    final String pgPath = "/pg/servicedefinitions/:id";

    parent.get(pgPath).handler(new GetPACServiceDefinitionFromDBHandler(db));
    parent.put(pgPath).handler(new PutPACServiceDefinitionDBHandler(db));
    parent.delete(pgPath).handler(new DeletePACServiceDefinitionDBHandler(db));

    final String pgPathAll = "/pg/servicedefinitions/";
    parent.get(pgPathAll).handler(new GetAllPACServiceDefinitionsFromDBHandler(db));
    parent.post(pgPathAll).handler(new PostPACServiceDefinitionDBHandler(db));

  }

  public static String getServiceDefinitionId(RoutingContext context) {
    var id = context.pathParam("id");
    LOG.debug("{} for account {}", context.normalizedPath(), id);
    return id;
  }
}
