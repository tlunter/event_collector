package com.upserve.event_collector.config;

import com.upserve.event_collector.stream.EventStreamer;
import com.upserve.event_collector.processing.Processor;

public class Config {
    public EventStreamer eventStreamer;
    public Processor processor;
}
