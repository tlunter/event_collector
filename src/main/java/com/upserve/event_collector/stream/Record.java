package com.upserve.event_collector.stream;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

@Slf4j
@Data
public class Record {
    private final String key;
    private final String data;

    Record(String key, String data) {
        this.key = key;
        this.data = data;
    }

    public static Record fromKinesisRecord(com.amazonaws.services.kinesis.model.Record kinesisRecord) {
        ByteBuffer byteBuffer = kinesisRecord.getData().asReadOnlyBuffer();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        try {
            return new Record(kinesisRecord.getPartitionKey(), new String(bytes, CharEncoding.UTF_8));
        } catch (UnsupportedEncodingException e) {
            log.warn("Could not convert Kinesis record: {}", kinesisRecord, e);
            return null;
        }
    }
}
