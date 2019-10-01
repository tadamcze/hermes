package pl.allegro.tech.hermes.frontend.validator;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.exception.ExceptionUtils;
import pl.allegro.tech.hermes.api.ContentType;
import pl.allegro.tech.hermes.api.Topic;
import pl.allegro.tech.hermes.common.message.converter.AvroRecordConverter;
import pl.allegro.tech.hermes.frontend.publishing.message.Message;

import javax.inject.Inject;

public class AvroTopicMessageValidator implements TopicMessageValidator {
    private final AvroRecordConverter avroRecordConverter;

    @Inject
    public AvroTopicMessageValidator(AvroRecordConverter avroRecordConverter) {
        this.avroRecordConverter = avroRecordConverter;
    }

    @Override
    public void check(Message message, Topic topic) {
        if (ContentType.AVRO != topic.getContentType() || (ContentType.JSON == topic.getContentType() && !topic.isJsonToAvroDryRunEnabled())) {
            return;
        }

        try {
            avroRecordConverter.avroBytesToRecord(message.getData(), message.getSchema());
        } catch (Exception e) {
            String reason = e.getMessage() == null ? ExceptionUtils.getRootCauseMessage(e) : e.getMessage();
            throw new InvalidMessageException("Could not deserialize avro message with provided schema", ImmutableList.of(reason));
        }
    }
}
