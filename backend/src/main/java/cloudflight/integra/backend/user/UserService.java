package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new
                ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
