package cloudflight.integra.backend.appevents;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.user.model.User;
import org.springframework.context.ApplicationEvent;

public class EventLeftEvent extends ApplicationEvent {
    private Event event;
    private User user;

    public EventLeftEvent(Object source, Event event, User user) {
        super(source);
        this.event = event;
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }
}
