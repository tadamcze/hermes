package pl.allegro.tech.hermes.common.message.converter;

import org.apache.avro.Schema;
import org.apache.avro.io.ResolvingDecoder;

class DisabledResolvingDecoderCache implements ResolvingDecoderCache {

    @Override
    public ResolvingDecoder get(Schema schema) {
        return null;
    }

    @Override
    public void put(Schema schema, ResolvingDecoder decoder) {

    }
}
