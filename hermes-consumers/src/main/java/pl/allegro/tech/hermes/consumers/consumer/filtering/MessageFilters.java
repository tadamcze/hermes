package pl.allegro.tech.hermes.consumers.consumer.filtering;

import pl.allegro.tech.hermes.api.MessageFilterSpecification;
import pl.allegro.tech.hermes.common.message.converter.AvroRecordConverter;
import pl.allegro.tech.hermes.consumers.consumer.filtering.avro.AvroPathSubscriptionMessageFilterCompiler;
import pl.allegro.tech.hermes.consumers.consumer.filtering.header.HeaderSubscriptionMessageFilterCompiler;
import pl.allegro.tech.hermes.consumers.consumer.filtering.json.JsonPathSubscriptionMessageFilterCompiler;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class MessageFilters implements MessageFilterSource {
    private Map<String, SubscriptionMessageFilterCompiler> filters;
    private List<MessageFilter> globalFilters;

    @Inject
    public MessageFilters(AvroRecordConverter avroRecordConverter, CustomizedMessageFilters customizedMessageFilters) {
        this.globalFilters = customizedMessageFilters.getGlobalFilters();
        List<SubscriptionMessageFilterCompiler> availableFilters = new ArrayList<>(customizedMessageFilters.getSubscriptionFilters());
        availableFilters.add(new JsonPathSubscriptionMessageFilterCompiler());
        availableFilters.add(new AvroPathSubscriptionMessageFilterCompiler(avroRecordConverter));
        availableFilters.add(new HeaderSubscriptionMessageFilterCompiler());
        this.filters = availableFilters.stream().collect(toMap(SubscriptionMessageFilterCompiler::getType, identity()));
    }

    @Override
    public MessageFilter compile(MessageFilterSpecification specification) {
        if (!filters.containsKey(specification.getType())) throw new NoSuchFilterException(specification.getType());
        return filters.get(specification.getType()).getMessageFilter(specification);
    }

    @Override
    public List<MessageFilter> getGlobalFilters() {
        return globalFilters;
    }
}
