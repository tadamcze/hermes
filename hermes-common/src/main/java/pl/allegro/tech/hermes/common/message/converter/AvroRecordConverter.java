package pl.allegro.tech.hermes.common.message.converter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvroRecordConverter {
    private final GenericDatumReaderFactory genericDatumReaderFactory;

    @Inject
    public AvroRecordConverter(GenericDatumReaderFactory genericDatumReaderFactory) {
        this.genericDatumReaderFactory = genericDatumReaderFactory;
    }

    public GenericRecord avroBytesToRecord(byte[] data, Schema schema) throws IOException {
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return genericDatumReaderFactory.create(schema).read(null, decoder);
    }

    public GenericRecord jsonBytesToRecord(byte[] bytes, Schema schema)  throws IOException  {
        InputStream input = new ByteArrayInputStream(bytes);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, input);
        return genericDatumReaderFactory.create(schema).read(null, decoder);
    }

    public byte [] recordToAvroBytes(GenericRecord genericRecord, Schema schema) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        new GenericDatumWriter<>(schema).write(genericRecord, binaryEncoder);
        binaryEncoder.flush();
        return outputStream.toByteArray();
    }
}
