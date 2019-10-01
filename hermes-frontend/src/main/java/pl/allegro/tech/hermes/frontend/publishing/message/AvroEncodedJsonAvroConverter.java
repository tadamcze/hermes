package pl.allegro.tech.hermes.frontend.publishing.message;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import pl.allegro.tech.hermes.common.message.converter.AvroRecordConverter;
import tech.allegro.schema.json2avro.converter.AvroConversionException;

import java.io.IOException;

class AvroEncodedJsonAvroConverter {
    private final AvroRecordConverter avroRecordConverter;

    AvroEncodedJsonAvroConverter(AvroRecordConverter avroRecordConverter) {
        this.avroRecordConverter = avroRecordConverter;
    }

    byte[] convertToAvro(byte[] bytes, Schema schema) {
        try {
            return convertToAvro(readJson(bytes, schema), schema);
        } catch (IOException e) {
            throw new AvroConversionException("Failed to convert to AVRO.", e);
        } catch (AvroRuntimeException e) {
                throw new AvroConversionException(
                        String.format("Failed to convert to AVRO: %s.", e.getMessage()), e);
        }
    }

    private GenericRecord readJson(byte[] bytes, Schema schema) throws IOException {
        return avroRecordConverter.jsonBytesToRecord(bytes, schema);
    }

    private byte[] convertToAvro(GenericRecord jsonData, Schema schema) throws IOException {
        return avroRecordConverter.recordToAvroBytes(jsonData, schema);
    }
}