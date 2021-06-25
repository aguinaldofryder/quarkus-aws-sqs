package org.acme.sqs;

import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class RuleMatrix {
    private static Map<Predicate, Function> ruleMatrix = new HashMap<>();

    static {
        ruleMatrix.put(QueuePredicates.WHEN_PRODUCT, QueueFunctions.CONVERT_PRODUCT);
        ruleMatrix.put(QueuePredicates.WHEN_CUSTUMER, QueueFunctions.CONVERT_CLIENT);
    }

    public static Function getRule(MessageAttributeValue attribute){
        return ruleMatrix
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().test(attribute))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(null);
    }
}
