package com.upserve.event_collector.stream;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsScope;
import com.upserve.event_collector.processing.Processor;
import lombok.Data;

@Data
public class EventStreamer {
    public String dynamoEndpoint;
    public String kinesisEndpoint;
    public Boolean collectMetrics;
    public String applicationName;
    public String streamName;

    private Worker worker;

    public void start(Processor processor) {
        KinesisClientLibConfiguration clientConfig = new KinesisClientLibConfiguration(
                applicationName,
                streamName,
                null,
                System.getenv("HOSTNAME")
        );
        Worker.Builder workerConfig = new Worker.Builder().config(clientConfig);
        if (dynamoEndpoint != null) {
            workerConfig.dynamoDBClient(new AmazonDynamoDBClient().withEndpoint(dynamoEndpoint));
        }
        if (kinesisEndpoint != null) {
            workerConfig.kinesisClient(new AmazonKinesisClient().withEndpoint(kinesisEndpoint));
        }
        if (collectMetrics != null && !collectMetrics) {
            workerConfig.metricsFactory(NullMetricsScope::new);
        }
        workerConfig.recordProcessorFactory(new EventRecordProcessorFactory(processor));

        this.worker = workerConfig.build();
        this.worker.run();
    }

    public void stop() {
        this.worker.shutdown();
    }
}
