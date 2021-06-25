package org.acme.sqs.resource;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/queue")
public class QueueResource {

    @Inject
    SqsClient sqs;

    /**
     * Lista todas as filas existentes
     * @return Retorna URL das filas
     */
    @GET
    public Response findAll() {
        ListQueuesResponse queues = sqs.listQueues();
        return Response.ok().entity(queues.queueUrls()).build();
    }

    /**
     * Retorna a url completa da fila, com base no nome
     * @param queueName Nome da fila
     * @return Retorna URL da fila
     */
    @GET
    @Path("{queueName}")
    public Response getUrl(@PathParam("queueName") String queueName) {
        String url = sqs.getQueueUrl(builder -> builder.queueName(queueName).build()).queueUrl();
        return Response.ok().entity(url).build();
    }

    /**
     * Cria uma nova fila
     * @param queueName Nome da fila
     * @return URL da fila
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String queueName) {
        Map<String, String> attributes= new HashMap<>();
        attributes.put("FifoQueue", "true");

        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .attributesWithStrings(attributes)
                .build();
        CreateQueueResponse result = sqs.createQueue(createQueueRequest);
        return Response.ok().entity(result.queueUrl()).build();
    }

    /**
     * @param queueName Nome da fila
     */
    @DELETE
    @Path("{queueName}")
    public Response delete(@PathParam("queueName") String queueName) {
        String url = sqs.getQueueUrl(builder -> builder.queueName(queueName).build()).queueUrl();
        sqs.deleteQueue(builder -> builder.queueUrl(url).build());
        return Response.ok().build();
    }
}