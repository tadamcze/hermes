package pl.allegro.tech.hermes.consumers.consumer.filtering;

import java.util.List;

public class CustomizedMessageFilters {
    private final List<MessageFilter> globalFilters;
    private final List<SubscriptionMessageFilterCompiler> subscriptionFilters;

    public CustomizedMessageFilters(List<MessageFilter> globalFilters,
                                    List<SubscriptionMessageFilterCompiler> subscriptionFilters) {
        this.globalFilters = globalFilters;
        this.subscriptionFilters = subscriptionFilters;
    }

    List<MessageFilter> getGlobalFilters() {
        return globalFilters;
    }

    List<SubscriptionMessageFilterCompiler> getSubscriptionFilters() {
        return subscriptionFilters;
    }
}
