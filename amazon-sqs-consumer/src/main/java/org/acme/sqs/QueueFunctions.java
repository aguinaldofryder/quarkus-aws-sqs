package org.acme.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.sqs.model.Customer;
import org.acme.sqs.model.Product;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

public class QueueFunctions {
    public static Function<Message, Product> CONVERT_PRODUCT = message -> {
        try {
            return new ObjectMapper().readerFor(Product.class).readValue(message.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    public static Function<Message, Customer> CONVERT_CLIENT = message -> {
        try {
            return new ObjectMapper().readerFor(Customer.class).readValue(message.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };
}
