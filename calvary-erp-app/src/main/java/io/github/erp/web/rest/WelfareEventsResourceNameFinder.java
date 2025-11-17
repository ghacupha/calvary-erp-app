package io.github.erp.web.rest;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Provides the set of welfare event identifiers that the server currently exposes.
 */
@Component
public class WelfareEventsResourceNameFinder {

    private static final List<String> EVENT_NAMES = List.of("membership-registration", "outreach-participation");

    /**
     * Returns the canonical identifiers for the welfare event resources.
     *
     * @return ordered list of event identifiers
     */
    public List<String> listEventNames() {
        return EVENT_NAMES;
    }
}
