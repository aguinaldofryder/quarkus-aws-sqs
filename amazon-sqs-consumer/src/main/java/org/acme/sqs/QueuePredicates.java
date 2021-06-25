package org.acme.sqs;

import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.function.Predicate;

public class QueuePredicates {
    public static Predicate<MessageAttributeValue> WHEN_PRODUCT = attribute -> attribute.stringValue().equals("product");
    public static Predicate<MessageAttributeValue> WHEN_CUSTUMER = attribute -> attribute.stringValue().equals("customer");
}
