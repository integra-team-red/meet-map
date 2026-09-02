package cloudflight.integra.backend.auth;

import cloudflight.integra.backend.auth.model.AuthResponse;
import cloudflight.integra.backend.auth.model.LoginRequest;
import cloudflight.integra.backend.auth.model.RegisterRequest;
import cloudflight.integra.backend.matrix.MatrixService;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final static Logger logger = LogManager.getLogger();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MatrixService matrixService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        MatrixService matrixService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.matrixService = matrixService;
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

        if(user.getMxId() == null) {
            matrixService.registerAccount(user);
        } else {
            logger.info("User already has a matrix account: {}, skipping creation", user.getMxId());
        }

        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole()));
    }

    private static String normalizeEmail(String email) {
        // Locale.ROOT: the default locale turns "I" into "ı" on a Turkish JVM
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
