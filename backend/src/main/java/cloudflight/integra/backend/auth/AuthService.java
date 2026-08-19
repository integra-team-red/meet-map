package cloudflight.integra.backend.auth;

import cloudflight.integra.backend.auth.model.AuthResponse;
import cloudflight.integra.backend.auth.model.LoginRequest;
import cloudflight.integra.backend.auth.model.RegisterRequest;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User()
            .setFirstName(request.firstName())
            .setLastName(request.lastName())
            .setEmail(email)
            .setPassword(passwordEncoder.encode(request.password()))
            .setBirthDate(request.birthDate())
            .setDescription(request.description());
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

    private static String normalizeEmail(String email) {
        // Locale.ROOT: the default locale turns "I" into "ı" on a Turkish JVM
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
