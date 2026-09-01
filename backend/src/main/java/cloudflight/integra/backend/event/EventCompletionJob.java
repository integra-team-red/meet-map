package cloudflight.integra.backend.event;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventCompletionJob {
    private static final Logger log = LoggerFactory.getLogger(EventCompletionJob.class);
    private final EventRepository repository;

    public EventCompletionJob(EventRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "${app.event-completion.cron}", zone = "Europe/Bucharest")
    @Transactional
    public void completePastEvents() {
        int updated = repository.markPastEventsCompleted(LocalDateTime.now());
        if (updated > 0) {
            log.info("Marked {} past events as COMPLETED", updated);
        }
    }
}
