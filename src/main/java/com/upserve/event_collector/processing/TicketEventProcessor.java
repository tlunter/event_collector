package com.upserve.event_collector.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upserve.event_collector.Redis;
import com.upserve.event_collector.stream.Event;
import com.upserve.event_collector.stream.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class TicketEventProcessor implements Processor {
    private static final ObjectMapper mapper = new ObjectMapper();
    private Redis redis;

    public void start(Redis redis) {
        this.redis = redis;
    }

    @Override
    public void process(String partition, List<Record> records, Runnable completer) {
        records.forEach((record) -> {
            try {
                Event event = mapper.readValue(record.getData(), Event.class);
                log.debug("Received event: {}", event);
            } catch (IOException e) {
                log.error("Could not parse record into event: {}", record, e);
            }
        });
    }
}
