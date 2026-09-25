package cloudflight.integra.backend.appevents;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.user.model.User;
import org.springframework.context.ApplicationEvent;

public class EventCreatedEvent extends ApplicationEvent {

    private Event event;
    private User creator;

    public EventCreatedEvent(Object source, Event event, User creator) {
        super(source);
        this.event = event;
        this.creator = creator;
    }

    public Event getEvent() {
        return event;
    }

    public User getCreator() {
        return creator;
    }
}
