package org.apache.mesos.kafka.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.mesos.kafka.scheduler.KafkaScheduler;
import org.apache.mesos.kafka.state.KafkaStateService;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/brokers")
@Produces("application/json")
public class BrokerController {
  private final Log log = LogFactory.getLog(BrokerController.class);
  private KafkaStateService state = KafkaStateService.getStateService();

  @GET
  public Response brokers() {
    try {
      JSONArray brokerIds = state.getBrokerIds();
      return Response.ok(brokerIds.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception ex) {
      log.error("Failed to fetch broker ids with exception: " + ex);
      return Response.serverError().build();
    }
  }

  @PUT
  public Response testTopic(@QueryParam("clean") String cleanString) {
    try {
      boolean clean = Boolean.parseBoolean(cleanString);
      List<String> taskIds = state.getTaskIds();
      KafkaScheduler.killTasks(taskIds);

      if (clean) {
        log.info("Cleaning ZK state for taskIds: " + taskIds);
      }

      return Response.ok(new JSONArray(taskIds).toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception ex) {
      log.error("Failed to kill brokers with exception: " + ex);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/{id}")
  public Response broker(@PathParam("id") String id) {
    try {
      JSONObject broker = state.getBroker(id);
      return Response.ok(broker.toString(), MediaType.APPLICATION_JSON).build();
    } catch (Exception ex) {
      log.error("Failed to fetch broker: " + id + " with exception: " + ex);
      return Response.serverError().build();
    }
  }
}
