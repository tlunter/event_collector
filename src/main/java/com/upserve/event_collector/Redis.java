package com.upserve.event_collector;

import com.fasterxml.jackson.annotation.JsonProperty;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

public class Redis {
    public final JedisPool pool;

    public Redis(@JsonProperty("endpoint") String endpoint) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1000);

        this.pool = new JedisPool(jedisPoolConfig, URI.create(endpoint));
    }
}
