package pl.allegro.tech.hermes.common.message.converter;

import com.codahale.metrics.MetricRegistry;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import pl.allegro.tech.hermes.common.config.ConfigFactory;

import javax.inject.Inject;

import static pl.allegro.tech.hermes.common.config.Configs.AVRO_DECODER_CACHE_MAX_SIZE;
import static pl.allegro.tech.hermes.common.config.Configs.AVRO_DECODER_CACHE_TYPE;

public class ConfigurableGenericDatumReaderFactory implements GenericDatumReaderFactory {
    private final AvroResolvingDecoderCacheType avroResolvingDecoderCacheType;
    private final DisabledResolvingDecoderCache disabledResolvingDecoderCache;
    private final LRUResolvingDecoderCache lruResolvingDecoderCache;

    @Inject
    public ConfigurableGenericDatumReaderFactory(ConfigFactory configFactory, MetricRegistry metricRegistry) {
        String avroCacheType = configFactory.getStringProperty(AVRO_DECODER_CACHE_TYPE).toUpperCase();
        avroResolvingDecoderCacheType = AvroResolvingDecoderCacheType.valueOf(avroCacheType);
        disabledResolvingDecoderCache = new DisabledResolvingDecoderCache();
        int cacheMaxSize = configFactory.getIntProperty(AVRO_DECODER_CACHE_MAX_SIZE);
        lruResolvingDecoderCache = new LRUResolvingDecoderCache(cacheMaxSize, metricRegistry);
    }

    @Override
    public GenericDatumReader<GenericRecord> create(Schema schema) {
        switch (avroResolvingDecoderCacheType) {
            case LRU:
                return new SingleSchemaGenericDatumReader<>(schema, lruResolvingDecoderCache);
            case NONE:
                return new SingleSchemaGenericDatumReader<>(schema, disabledResolvingDecoderCache);
            case WEAK_REFERENCE:
            default:
                return new GenericDatumReader<>(schema);
        }
    }
}
