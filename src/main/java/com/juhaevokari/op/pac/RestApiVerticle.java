package com.juhaevokari.op.pac;

import com.juhaevokari.op.pac.config.ConfigLoader;
import com.juhaevokari.op.pac.config.PACServiceDefinitionConfig;
import com.juhaevokari.op.pac.servicedefinitions.PACServiceDefinitionRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved configuration: " + configuration);
        startHttpServerandAttachRoutes(startPromise, configuration);
      });
  }

  private void startHttpServerandAttachRoutes(final Promise<Void> startPromise, final PACServiceDefinitionConfig configuration) {

    final Pool db = createDBPool(configuration);

    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create());

    handleFailure(restApi);
    PACServiceDefinitionRestApi.attach(restApi, db);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server Error: ", error))
      .listen(configuration.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}.", configuration.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private PgPool createDBPool(PACServiceDefinitionConfig configuration) {
    final PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setPort(configuration.getDbConfig().getPort())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    //for demo only, maxSize 4 is default:
    final PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(4);

    final PgPool db = PgPool.pool(vertx, pgConnectOptions, poolOptions);
    return db;
  }

  private Route handleFailure(Router restApi) {
    return restApi.route().failureHandler(errorContext -> {
      if (errorContext.response().ended()) {
        //ignore client cancel:
        return;
      }
      LOG.error("Route error: ", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong").toBuffer());
    });
  }


}
