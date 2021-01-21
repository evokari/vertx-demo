package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.db.DBResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostPACServiceDefinitionDBHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(GetPACServiceDefinitionFromDBHandler.class);
    private final Pool db;

    public PostPACServiceDefinitionDBHandler(Pool db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        var serviceDefinitionId = UUID.randomUUID().toString();
        var json = context.getBodyAsJson();
        var serviceDefinition = json.mapTo(PACServiceDefinition.class);
        serviceDefinition.setId(serviceDefinitionId);

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", serviceDefinitionId);
        parameters.put("name", serviceDefinition.getName());
        parameters.put("description", serviceDefinition.getDescription());
        parameters.put("status", serviceDefinition.getStatus());

        SqlTemplate.forUpdate(db,
                "INSERT INTO pac.servicedefinition VALUES (#{id},#{name},#{description},#{status})")
            .execute(parameters)
            .onFailure(DBResponse.errorHandler(context, "Failed to create new service definition"))
            .onSuccess(result -> {
                LOG.debug("Created new service definition with id: " + serviceDefinitionId);
                context.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .end(serviceDefinition.toJsonObject().toBuffer());
                });
    }

}

