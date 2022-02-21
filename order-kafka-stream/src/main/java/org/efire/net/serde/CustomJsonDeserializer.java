package org.efire.net.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Objects;

public class CustomJsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> target;

    public CustomJsonDeserializer(Class<T> target) {
        Objects.requireNonNull(target, "Should not be null");
        this.target = target;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, target);
        } catch (IOException e) {
            throw new SerializationException("Failed in deserialization", e);
        }
    }
}
