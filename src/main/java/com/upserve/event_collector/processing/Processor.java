package com.upserve.event_collector.processing;

import com.upserve.event_collector.stream.Record;

import java.util.List;

public interface Processor {
    void process(String partition, List<Record> records, Runnable completer);
}
