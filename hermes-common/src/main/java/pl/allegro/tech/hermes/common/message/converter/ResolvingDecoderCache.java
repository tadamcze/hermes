package pl.allegro.tech.hermes.common.message.converter;

import org.apache.avro.Schema;
import org.apache.avro.io.ResolvingDecoder;

interface ResolvingDecoderCache {
    ResolvingDecoder get(Schema schema);
    void put(Schema schema, ResolvingDecoder decoder);
}
