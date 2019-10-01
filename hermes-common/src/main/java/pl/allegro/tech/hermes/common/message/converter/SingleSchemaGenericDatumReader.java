package pl.allegro.tech.hermes.common.message.converter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.ResolvingDecoder;

import java.io.IOException;

class SingleSchemaGenericDatumReader<D> extends GenericDatumReader<D> {
    private final ResolvingDecoderCache cache;
    private final Schema schema;

    SingleSchemaGenericDatumReader(Schema schema, ResolvingDecoderCache cache) {
        super(schema);
        this.schema = schema;
        this.cache = cache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public D read(D reuse, Decoder in) throws IOException {
        ResolvingDecoder resolver = getResolver();
        resolver.configure(in);
        D result = (D) read(reuse, schema, resolver);
        resolver.drain();
        return result;
    }

    private ResolvingDecoder getResolver() throws IOException {
        ResolvingDecoder resolver = cache.get(schema);
        if (resolver == null) {
            resolver = DecoderFactory.get().resolvingDecoder(Schema.applyAliases(schema, schema), schema, null);
            cache.put(schema, resolver);
        }
        return resolver;
    }
}
