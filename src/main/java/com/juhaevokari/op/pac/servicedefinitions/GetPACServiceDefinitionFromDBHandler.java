package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.db.DBResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetPACServiceDefinitionFromDBHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetPACServiceDefinitionFromDBHandler.class);
  private final Pool db;

  public GetPACServiceDefinitionFromDBHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var serviceDefinitionId = PACServiceDefinitionRestApi.getServiceDefinitionId(context);

    SqlTemplate.forQuery(db,
            "SELECT s.id, s.name, s.description, s.status FROM pac.servicedefinition s where s.id=#{id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("id", serviceDefinitionId))
      .onFailure(DBResponse.errorHandler(context, "Failed to fetch PAC definition data for serviceDefinitionId: " + serviceDefinitionId))
      .onSuccess(definitions -> {
        if (!definitions.iterator().hasNext()) {
          DBResponse.notFound(context, "PAC definition for serviceDefinitionId " + serviceDefinitionId + " is not available.");
          return;
        }
        //we only get one:
        var response = definitions.iterator().next();
        LOG.info("Path {} responds with {}.", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
          .end(response.toBuffer());
      });
  }
}
