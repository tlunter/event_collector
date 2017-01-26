package com.upserve.event_collector.config;

import com.upserve.event_collector.Redis;
import com.upserve.event_collector.processing.TicketEventProcessor;
import com.upserve.event_collector.stream.EventStreamer;
import com.upserve.event_collector.processing.Processor;

public class Config {
    public Redis redis;
    public EventStreamer eventStreamer;
    public TicketEventProcessor ticketEventProcessor;
}
