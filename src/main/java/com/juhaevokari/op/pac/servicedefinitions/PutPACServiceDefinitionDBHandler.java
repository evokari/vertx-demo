package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.db.DBResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PutPACServiceDefinitionDBHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetPACServiceDefinitionFromDBHandler.class);
  private final Pool db;
  public PutPACServiceDefinitionDBHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var serviceDefinitionId = PACServiceDefinitionRestApi.getServiceDefinitionId(context);
    var json = context.getBodyAsJson();
    var serviceDefinition = json.mapTo(PACServiceDefinition.class);


        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", serviceDefinitionId);
        parameters.put("name", serviceDefinition.getName());
        parameters.put("description", serviceDefinition.getDescription());
        parameters.put("status", serviceDefinition.getStatus());


    db.withTransaction(client -> {
      // 1- delete all
      return SqlTemplate.forUpdate(db,"DELETE FROM pac.servicedefinition s where s.id=#{id}")
        .execute(Collections.singletonMap("id", serviceDefinitionId))
        .onFailure(DBResponse.errorHandler(context, "Failed to clear pac service definition for id: " + serviceDefinitionId))
        .compose(deletionDone -> {
          //2 - Add all the entries from PUT request
          return addDataForServiceDefinition(client, context, parameters);
        })

        .onFailure(DBResponse.errorHandler(context, "Failed to update service definition for id: " + serviceDefinitionId))
        // 3 - Both done
        .onSuccess(result -> {
          context.response()
            .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
            .end();
        });
    });
  }

  private Future<SqlResult<Void>> addDataForServiceDefinition(SqlConnection client, RoutingContext context, Map<String, Object> parameters) {
    return SqlTemplate.forUpdate(client,
      "INSERT INTO pac.servicedefinition VALUES (#{id},#{name},#{description},#{status})"
        + " ON CONFLICT (id) DO NOTHING"
    )
      .execute(parameters)
      .onFailure(DBResponse.errorHandler(context, "Failed to insert into service definitions."));

  }
}
