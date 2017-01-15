package com.upserve.event_collector.stream;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.upserve.event_collector.processing.Processor;

public class EventRecordProcessorFactory implements IRecordProcessorFactory {
    private final Processor processor;

    public EventRecordProcessorFactory(Processor processor) {
        this.processor = processor;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new EventRecordProcessor(processor);
    }
}
