package io.github.erp.web.rest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight REST controller that describes the welfare event resources exposed by the API.
 */
@RestController
@RequestMapping("/api/welfare-events")
public class WelfareEventsResource {

    private static final Logger LOG = LoggerFactory.getLogger(WelfareEventsResource.class);

    private final WelfareEventsResourceNameFinder welfareEventsResourceNameFinder;
    private final WelfareEventsResourceDetailsMapper welfareEventsResourceDetailsMapper;

    public WelfareEventsResource(
        WelfareEventsResourceNameFinder welfareEventsResourceNameFinder,
        WelfareEventsResourceDetailsMapper welfareEventsResourceDetailsMapper
    ) {
        this.welfareEventsResourceNameFinder = welfareEventsResourceNameFinder;
        this.welfareEventsResourceDetailsMapper = welfareEventsResourceDetailsMapper;
    }

    /**
     * {@code GET  /welfare-events} : describe the available welfare event resources.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listEventResources() {
        LOG.debug("REST request to list welfare event resources");
        List<Map<String, Object>> payload = welfareEventsResourceNameFinder
            .listEventNames()
            .stream()
            .map(welfareEventsResourceDetailsMapper::toDetails)
            .toList();

        return ResponseEntity.ok(payload);
    }

    /**
     * {@code GET  /welfare-events/:eventName} : get details for a single welfare event resource.
     */
    @GetMapping("/{eventName}")
    public ResponseEntity<Map<String, Object>> getEventDetails(@PathVariable String eventName) {
        LOG.debug("REST request for welfare event resource details: {}", eventName);

        Optional<Map<String, Object>> details = welfareEventsResourceNameFinder
            .listEventNames()
            .stream()
            .filter(name -> name.equalsIgnoreCase(eventName))
            .findFirst()
            .map(welfareEventsResourceDetailsMapper::toDetails);

        return details.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
