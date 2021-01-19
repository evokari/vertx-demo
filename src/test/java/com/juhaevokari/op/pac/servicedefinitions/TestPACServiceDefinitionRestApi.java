package com.juhaevokari.op.pac.servicedefinitions;

import com.juhaevokari.op.pac.AbstractRestApiTest;
import com.juhaevokari.op.pac.MainVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestPACServiceDefinitionRestApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  final UUID serviceDefinitionId = UUID.randomUUID();


  @Test
  void adds_and_returns_service_definition(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = webclient(vertx);
    AtomicReference<String> id = new AtomicReference<>();
    client.post("/pg/servicedefinitions/")
      .sendJsonObject(body())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("POST Response: {}", json);
        assertEquals("{\"id\":\""+ json.getString("id") +"\",\"name\":\"name\",\"description\":\"this is description\",\"status\":\"active\"}", json.encode());
        assertEquals(200, response.statusCode());
        id.set(json.getString("id"));
      })).compose(next -> {
      client.get("/pg/servicedefinitions/" + id)
        .send()
        .onComplete(testContext.succeeding(response -> {
          var json = response.bodyAsJsonObject();
          LOG.info("GET Response: {}", json);
          assertEquals("{\"id\":\"" + id + "\",\"name\":\"name\",\"description\":\"this is description\",\"status\":\"active\"}", json.encode());
          assertEquals(200, response.statusCode());
          testContext.completeNow();
        }));
      return Future.succeededFuture();
    });
  }

  @Test
  void adds_and_updates_service_definition(Vertx vertx, VertxTestContext testContext) throws Throwable {
      var client = webclient(vertx);
      AtomicReference<String> id = new AtomicReference<>();
      client.post("/pg/servicedefinitions/")
          .sendJsonObject(body())
          .onComplete(testContext.succeeding(response -> {
              var json = response.bodyAsJsonObject();
              LOG.info("POST Response: {}", json);
              assertEquals("{\"id\":\""+ json.getString("id") +"\",\"name\":\"name\",\"description\":\"this is description\",\"status\":\"active\"}", json.encode());
              assertEquals(200, response.statusCode());
              id.set(json.getString("id"));
          })).compose(next -> {
              client.put("/pg/servicedefinitions/" + id)
                  .sendJsonObject(body())
                  .onComplete(testContext.succeeding(response -> {
                      assertEquals(204, response.statusCode());
                      testContext.completeNow();
                  }));
          return Future.succeededFuture();
      });
  }

    @Test
    void adds_and_deletes_service_definition(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var client = webclient(vertx);
        AtomicReference<String> id = new AtomicReference<>();
        client.post("/pg/servicedefinitions/")
                .sendJsonObject(body())
                .onComplete(testContext.succeeding(response -> {
                    var json = response.bodyAsJsonObject();
                    LOG.info("POST Response: {}", json);
                    assertEquals("{\"id\":\""+ json.getString("id") +"\",\"name\":\"name\",\"description\":\"this is description\",\"status\":\"active\"}", json.encode());
                    assertEquals(200, response.statusCode());
                    id.set(json.getString("id"));
                })).compose(next -> {
            client.delete("/pg/servicedefinitions/" + id)
                    .send()
                    .onComplete(testContext.succeeding(response -> {
                        assertEquals(204, response.statusCode());
                        testContext.completeNow();
                    }));
            return Future.succeededFuture();
        });
    }

  private JsonObject body() {
    return new PACServiceDefinition("name", "this is description", "active")
      .toJsonObject();
  }
}
