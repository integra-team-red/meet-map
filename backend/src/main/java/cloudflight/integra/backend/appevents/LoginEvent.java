package cloudflight.integra.backend.appevents;

import cloudflight.integra.backend.user.model.User;
import org.springframework.context.ApplicationEvent;

public class LoginEvent extends ApplicationEvent {
    private User user;

    public LoginEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
