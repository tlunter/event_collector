package com.upserve.event_collector;

import com.upserve.event_collector.config.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Engine implements AutoCloseable {
    public final Config config;

    public Engine(Config config) {
        this.config = config;
    }

    public void run() {
        config.ticketEventProcessor.start(config.redis);

        config.eventStreamer.start(config.ticketEventProcessor);
    }

    @Override
    public void close() throws Exception {
        config.eventStreamer.stop();
    }
}
