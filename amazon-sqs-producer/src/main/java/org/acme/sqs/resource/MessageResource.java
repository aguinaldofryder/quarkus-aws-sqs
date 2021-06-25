package org.acme.sqs.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.acme.sqs.model.Product;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/queue/message")
public class MessageResource {

    private static final Logger LOGGER = Logger.getLogger(QueueResource.class);

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    @Inject
    SqsClient sqs;

    static ObjectWriter QUARK_WRITER = new ObjectMapper().writerFor(Product.class);

    @POST
    public Response send(Product product) throws Exception {
        Map<String, MessageAttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put("type", MessageAttributeValue.builder()
                .dataType("String")
                .stringValue("product")
                .build());

        product.setId(UUID.randomUUID().toString());
        String textMessage = QUARK_WRITER.writeValueAsString(product);
        SendMessageResponse response = sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageGroupId("product")
                .messageDeduplicationId(UUID.randomUUID().toString())
                .messageBody(textMessage)
                .messageAttributes(attributeValueMap)
                .build());
        LOGGER.infov("Fired Quark[{0}]", textMessage);
        return Response.ok().build();
    }
}
