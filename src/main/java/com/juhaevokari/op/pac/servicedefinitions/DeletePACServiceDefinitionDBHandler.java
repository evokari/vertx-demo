package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.db.DBResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DeletePACServiceDefinitionDBHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetPACServiceDefinitionFromDBHandler.class);
  Pool db;

  public DeletePACServiceDefinitionDBHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var serviceDefinitionId = PACServiceDefinitionRestApi.getServiceDefinitionId(context);

    SqlTemplate.forUpdate(db,
      "DELETE FROM pac.servicedefinition WHERE id=#{id}")
      .execute(Collections.singletonMap("id", serviceDefinitionId))
      .onFailure(DBResponse.errorHandler(context, "Failed to delete service definition for id: " + serviceDefinitionId))
      .onSuccess(result -> {
         LOG.debug("Deleted {} rows for accountId {}", result.rowCount(), serviceDefinitionId);
         context.response()
           .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
           .end();
      });

  }
}
