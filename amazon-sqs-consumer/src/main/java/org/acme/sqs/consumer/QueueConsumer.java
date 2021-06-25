package org.acme.sqs.consumer;


import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import org.acme.sqs.RuleMatrix;
import org.acme.sqs.converter.ProductConverter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@ApplicationScoped
public class QueueConsumer {

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    @ApplicationScoped
    ProductConverter productConverter;

    private boolean isProcessing = false;

    @Scheduled(every = "1s")
    public void auto() {
        if(isProcessing) {
            return;
        }

        System.out.println("Buscando novas mensagens...");
        try {
            isProcessing = true;

            List<Message> messages = sqs.receiveMessage(m -> m
                    .messageAttributeNames("All")
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .queueUrl(queueUrl)
            ).messages();
            System.out.println("Concluiu a busca de mensagens...");

            for (Message message : messages) {

                Map<String, MessageAttributeValue> attributes = message.messageAttributes();
                MessageAttributeValue type = attributes.get("type");

                Object bean = RuleMatrix.getRule(type).apply(message);

                sqs.deleteMessage(builder -> builder.queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle()).build());
            }
        }catch(Exception ex) {
            System.out.println("Erro ao receber mensagem: " + ex.getLocalizedMessage());
        } finally {
            isProcessing= false;
        }
    }
}
