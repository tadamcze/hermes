package pl.allegro.tech.hermes.common.message.converter;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.apache.avro.Schema;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.commons.collections4.map.LRUMap;

import static com.codahale.metrics.MetricRegistry.name;

class LRUResolvingDecoderCache implements ResolvingDecoderCache {
    private static final String CACHE_METRICS_ROOT = "avro.resolving-decoder.cache.lru";

    private static final ThreadLocal<LRUMap<Schema, ResolvingDecoder>> cache = new ThreadLocal<>();

    private final int maxSize;
    private final Meter cacheRequestMeter;
    private final Meter cacheHitMeter;
    private final Meter cacheMissMeter;

    LRUResolvingDecoderCache(int maxSize, MetricRegistry metricRegistry) {
        this.maxSize = maxSize;
        this.cacheRequestMeter = metricRegistry.meter(name(CACHE_METRICS_ROOT, "request"));
        this.cacheHitMeter = metricRegistry.meter(name(CACHE_METRICS_ROOT, "hit"));
        this.cacheMissMeter = metricRegistry.meter(name(CACHE_METRICS_ROOT, "miss"));
    }

    @Override
    public ResolvingDecoder get(Schema schema) {
        cacheRequestMeter.mark();
        LRUMap<Schema, ResolvingDecoder> resolvingDecoders = cache.get();
        if (resolvingDecoders != null) {
            ResolvingDecoder resolvingDecoder = resolvingDecoders.get(schema);
            if (resolvingDecoder != null) {
                cacheHitMeter.mark();
                return resolvingDecoder;
            }
        }
        cacheMissMeter.mark();
        return null;
    }

    @Override
    public void put(Schema schema, ResolvingDecoder decoder) {
        LRUMap<Schema, ResolvingDecoder> resolvingDecoders = cache.get();
        if (resolvingDecoders == null) {
            resolvingDecoders = new LRUMap<>(maxSize);
        }
        resolvingDecoders.put(schema, decoder);
        cache.set(resolvingDecoders);
    }
}
