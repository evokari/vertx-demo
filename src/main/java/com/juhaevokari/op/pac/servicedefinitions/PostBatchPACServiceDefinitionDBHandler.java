package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.db.DBResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostBatchPACServiceDefinitionDBHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PostBatchPACServiceDefinitionDBHandler.class);
    private final Pool db;

    public PostBatchPACServiceDefinitionDBHandler(Pool db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        var definitionArray = context.getBodyAsJsonArray();
        var parameterBatch = definitionArray.stream()
            .map(definitionAsJson -> {
                var definition = new JsonObject(definitionAsJson.toString()).mapTo(PACServiceDefinition.class);
                final Map<String, Object> parameters = new HashMap<>();
                parameters.put("id", UUID.randomUUID().toString());
                parameters.put("name", definition.getName());
                parameters.put("description", definition.getDescription());
                parameters.put("status", definition.getStatus());
                return parameters;
            }).collect(Collectors.toList());

        db.withTransaction(client -> {
            // 1- delete all
            return SqlTemplate.forUpdate(client, "INSERT INTO pac.servicedefinition VALUES (#{id},#{name},#{description},#{status})")
                .executeBatch(parameterBatch)
                .onFailure(DBResponse.errorHandler(context, "Failed to insert into service definitions."))
                .onSuccess(result -> {
                    context.response()
                        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
                        .end();
                    });
        });
    }
}