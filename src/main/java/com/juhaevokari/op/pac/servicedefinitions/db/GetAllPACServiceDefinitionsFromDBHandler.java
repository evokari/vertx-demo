package com.juhaevokari.op.pac.servicedefinitions.db;

import com.juhaevokari.op.pac.db.DBResponse;
import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinition;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetAllPACServiceDefinitionsFromDBHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(GetAllPACServiceDefinitionsFromDBHandler.class);
    private final Pool db;

    public GetAllPACServiceDefinitionsFromDBHandler(Pool db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {

        SqlTemplate.forQuery(db, "SELECT s.id, s.name, s.description, s.status FROM pac.servicedefinition s")
            .mapTo(Row::toJson)
            .execute(Collections.singletonMap ("Service", PACServiceDefinition.class))
            .onFailure(DBResponse.errorHandler(context, "Failed to fetch service definitions."))
            .onSuccess(definitions -> {
                if (!definitions.iterator().hasNext()) {
                    DBResponse.notFound(context, "Service definitions are not available.");
                    return;
                }
                var response = new JsonArray();
                definitions.forEach(response::add);
                LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
                context.response()
                        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .end(response.toBuffer());
            });
    }
}
