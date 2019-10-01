package pl.allegro.tech.hermes.common.message.converter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;

public interface GenericDatumReaderFactory {
    GenericDatumReader<GenericRecord> create(Schema schema);
}
