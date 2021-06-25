package org.acme.sqs.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AbstractConverter<T> {
    static ObjectReader READER = new ObjectMapper().readerFor(getGenericType());

    public T toModel(String value) {
        try {
            return READER.readValue(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retorna o generic declarado na interface
     */
    static Class<?> getGenericType() {
        final Type[] genericInterfaces = AbstractConverter.class.getGenericInterfaces();
        for (final Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) genericInterface;

                if (AbstractConverter.class.equals(parameterizedType.getRawType())) {
                    final Type[] genericTypes = parameterizedType.getActualTypeArguments();
                    return (Class<?>) genericTypes[0];
                }
            }
        }

        final Type t = AbstractConverter.class.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) t;
            return (Class<?>) pt.getActualTypeArguments()[0];
        }

        return null;
    }
}
