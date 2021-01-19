package com.juhaevokari.op.pac.db;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBResponse {

  private static final Logger LOG = LoggerFactory.getLogger(DBResponse.class);

  public static Handler<Throwable> errorHandler(RoutingContext context, String message) {
    return error -> {
      LOG.error("Failure: ", error);
      context.response()
        .setStatusCode(500)
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(
          new JsonObject()
            .put("message", message)
            .put("path", context.normalizedPath())
            .toBuffer()
        );
    };
  }

  public static void notFound(RoutingContext context, String message) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(
          new JsonObject()
            .put("message", message)
            .put("path", context.normalizedPath())
            .toBuffer()
        );
  }
}