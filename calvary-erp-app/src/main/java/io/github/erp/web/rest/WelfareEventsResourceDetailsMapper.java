package io.github.erp.web.rest;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Maps welfare event identifiers to a simple detail payload for API consumers.
 */
@Component
public class WelfareEventsResourceDetailsMapper {

    /**
     * Build a detail payload for the provided welfare event identifier.
     *
     * @param eventName canonical event name
     * @return map containing the event name and description
     */
    public Map<String, Object> toDetails(String eventName) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("name", eventName);
        details.put("description", descriptionFor(eventName));
        return details;
    }

    private String descriptionFor(String eventName) {
        return switch (eventName) {
            case "membership-registration" -> "Welfare membership registration submissions.";
            case "outreach-participation" -> "Tracking interest in outreach or support events.";
            default -> "General welfare event.";
        };
    }
}
