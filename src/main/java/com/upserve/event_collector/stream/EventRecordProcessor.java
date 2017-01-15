package com.upserve.event_collector.stream;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IShutdownNotificationAware;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.upserve.event_collector.processing.Processor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class EventRecordProcessor implements IRecordProcessor, IShutdownNotificationAware {
    private final Processor processor;
    private String shardId;

    public EventRecordProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.shardId = initializationInput.getShardId();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        Runnable checkpoint = () -> {
            try {
                processRecordsInput.getCheckpointer().checkpoint();
            } catch (InvalidStateException | ShutdownException e) {
                handleCheckpointFailure(e);
            }
        };

        List<Record> records = processRecordsInput.getRecords()
                .stream()
                .map(Record::fromKinesisRecord)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        processor.process(shardId, records, checkpoint);
    }

    @Override
    public void shutdownRequested(IRecordProcessorCheckpointer checkpointer) {
        try {
            checkpointer.checkpoint();
        } catch (InvalidStateException | ShutdownException e) {
            handleCheckpointFailure(e);
        }
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        if (shutdownInput.getShutdownReason().equals(ShutdownReason.TERMINATE)) {
            try {
                shutdownInput.getCheckpointer().checkpoint();
            } catch (InvalidStateException | ShutdownException e) {
                handleCheckpointFailure(e);
            }
        } else {
            log.warn("Shutting down record processor for {} because of {}", shardId, shutdownInput.getShutdownReason());
        }
    }

    private void handleCheckpointFailure(Exception ex) {
        log.error("Exception hit while checkpointing " + shardId, ex);
    }
}
