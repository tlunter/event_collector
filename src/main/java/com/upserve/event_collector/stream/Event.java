package com.upserve.event_collector.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Event {
    @JsonProperty("event_name")
    String eventName;

    @JsonProperty("event_data")
    Map<String, Object> eventData;
}
