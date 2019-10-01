package pl.allegro.tech.hermes.common.message.converter

import com.codahale.metrics.MetricRegistry
import org.apache.avro.util.Utf8
import pl.allegro.tech.hermes.common.config.ConfigFactory
import pl.allegro.tech.hermes.test.helper.avro.AvroUser
import spock.lang.Specification
import spock.lang.Unroll

import static pl.allegro.tech.hermes.common.config.Configs.AVRO_DECODER_CACHE_MAX_SIZE
import static pl.allegro.tech.hermes.common.config.Configs.AVRO_DECODER_CACHE_TYPE

class AvroRecordConverterTest extends Specification {

    def avroUser = new AvroUser("Bob", 10, "red")
    def configFactory = Stub(ConfigFactory)

    def setup() {
        configFactory.getIntProperty(AVRO_DECODER_CACHE_MAX_SIZE) >> 10
    }

    @Unroll
    def "should convert avro bytes to record using cache: '#cacheType'"() {
        given:
        configFactory.getStringProperty(AVRO_DECODER_CACHE_TYPE) >> cacheType
        def recordConverter = new AvroRecordConverter(new ConfigurableGenericDatumReaderFactory(configFactory, new MetricRegistry()))

        when:
        def record = recordConverter.avroBytesToRecord(avroUser.asBytes(), avroUser.getSchema())

        then:
        record.get("name") == new Utf8(avroUser.name)
        record.get("age") == avroUser.age
        record.get("favoriteColor") == new Utf8(avroUser.favoriteColor)

        where:
        cacheType << ["none", "weak_reference", "lru"]
    }

    @Unroll
    def "should convert json bytes to record using cache: '#cacheType'"() {
        given:
        configFactory.getStringProperty(AVRO_DECODER_CACHE_TYPE) >> cacheType
        def recordConverter = new AvroRecordConverter(new ConfigurableGenericDatumReaderFactory(configFactory, new MetricRegistry()))

        when:
        def record = recordConverter.jsonBytesToRecord(avroUser.asAvroEncodedJson().bytes, avroUser.getSchema())

        then:
        record.get("name") == new Utf8(avroUser.name)
        record.get("age") == avroUser.age
        record.get("favoriteColor") == new Utf8(avroUser.favoriteColor)

        where:
        cacheType << ["none", "weak_reference", "lru"]
    }
}
