package org.myorg.quickstart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;

import java.io.IOException;

public class TransactionDeserializationSchema extends AbstractDeserializationSchema<Transaction> {
    private static final long serialVersionUUID = 1L;

    private transient ObjectMapper objectMapper;

    @Override
    public void open(InitializationContext context) {
        objectMapper = JsonMapper.builder().build().registerModule(new JavaTimeModule());
    }

    @Override
    public Transaction deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, Transaction.class);
    }
}
