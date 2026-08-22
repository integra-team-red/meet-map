package cloudflight.integra.backend.databaseseed;

import cloudflight.integra.backend.auth.AuthService;
import cloudflight.integra.backend.auth.model.RegisterRequest;
import cloudflight.integra.backend.event.EventRepository;
import cloudflight.integra.backend.event.EventService;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.eventparticipation.EventParticipationRepository;
import cloudflight.integra.backend.eventparticipation.EventParticipationService;
import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import cloudflight.integra.backend.flag.FlagRepository;
import cloudflight.integra.backend.flag.FlagService;
import cloudflight.integra.backend.flag.model.Flag;
import cloudflight.integra.backend.review.ReviewRepository;
import cloudflight.integra.backend.review.ReviewService;
import cloudflight.integra.backend.review.model.Review;
import cloudflight.integra.backend.tag.TagRepository;
import cloudflight.integra.backend.tag.TagService;
import cloudflight.integra.backend.tag.model.Category;
import cloudflight.integra.backend.tag.model.Tag;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class DatabaseSeedService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final FlagRepository flagRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    private final EventService eventService;
    private final EventParticipationService eventParticipationService;
    private final FlagService flagService;
    private final TagService tagService;
    private final ReviewService reviewService;

    private final Faker faker = new Faker();

    public DatabaseSeedService(
        UserRepository userRepository,
        AuthService authService,
        EventRepository eventRepository,
        EventParticipationRepository eventParticipationRepository,
        FlagRepository flagRepository,
        TagRepository tagRepository,
        ReviewRepository reviewRepository,
        EventService eventService,
        EventParticipationService eventParticipationService,
        FlagService flagService,
        TagService tagService,
        ReviewService reviewService
    ) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.eventRepository = eventRepository;
        this.eventParticipationRepository = eventParticipationRepository;
        this.flagRepository = flagRepository;
        this.tagRepository = tagRepository;
        this.reviewRepository = reviewRepository;
        this.eventService = eventService;
        this.eventParticipationService = eventParticipationService;
        this.flagService = flagService;
        this.tagService = tagService;
        this.reviewService = reviewService;
    }

    public void seedDatabase() {
        seedUserTable();
        seedTagTable();
        seedEventTable();
        seedFlagTable();
        seedReviewTable();
        seedEventParticipationTable();
    }

    public void clearDatabase() {
        reviewRepository.deleteAll();
        flagRepository.deleteAll();
        eventParticipationRepository.deleteAll();
        eventRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }

    public void seedUserTable() {
        RegisterRequest testUserReq = new RegisterRequest(
            "Test",
            "User",
            "test@test.com",
            "Password123",
            LocalDate.of(2000, 05, 10),
            "Description_Test"
        );
        try {
            authService.register(testUserReq);
        } catch (Exception e) {
            System.out.println("Failed to register user: " + e.getMessage());
        }
        for (int i = 0; i < 5; i++) {
            RegisterRequest userReq = new RegisterRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().safeEmailAddress(),
                "Password123",
                LocalDate.ofInstant(faker.date().birthday().toInstant(), ZoneId.systemDefault()),
                faker.lorem().paragraph()
            );
            try {
                authService.register(userReq);
            } catch (Exception e) {
                System.out.println("Failed to register user: " + e.getMessage());
            }
        }
    }

    public void seedTagTable() {
        if (!tagService.getAll().isEmpty()) {
            return;
        }

        Set<String> usedNames = new HashSet<>();
        int attempts = 0;
        while (usedNames.size() < 10 && attempts < 100) {
            attempts++;
            Tag tag = new Tag();
            tag.setCategory(faker.options().option(Category.class));
            switch (tag.getCategory()) {
                case PERSONALITY ->
                    tag.setName(faker.options()
                        .option("Introvert", "Extrovert", "Ambivert", "Analytical", "Creative", "Pragmatic"));
                case HOBBY ->
                    tag.setName(faker.options()
                        .option("Reading", "Traveling", "Photography", "Cooking", "Gaming", "Hiking"));
                case EVENT_TYPE ->
                    tag.setName(faker.options()
                        .option("Conference", "Seminar", "Workshop", "Meetup", "Hackathon", "Webinar",
                            "Networking"));
            }
            if (!usedNames.add(tag.getName())) {
                continue;
            }
            try {
                tagService.create(tag);
            } catch (Exception e) {
                System.out.println("Failed to save tag: " + e.getMessage());
            }
        }
    }

    public void seedEventTable() {
        if (!eventService.getAll(Pageable.unpaged()).isEmpty()) {
            return;
        }
        List<Tag> allTags = tagService.getAll();
        if (allTags.isEmpty()) {
            return;
        }
        for (int i = 0; i < 25; i++) {
            Event event = new Event();
            event.setTitle(faker.book().title());
            event.setDescription(faker.lorem().paragraph());
            event.setAddress(faker.address().streetAddress());
            event.setCity(faker.address().city());
            event.setLatitude(faker.number().randomDouble(4, 0, 90));
            event.setLongitude(faker.number().randomDouble(4, 0, 90));
            event.setDateTime(LocalDateTime.of(
                2026, 7, faker.number().numberBetween(1, 31), 12, 0));
            event.setMaxParticipants(faker.number().numberBetween(0, 150));
            event.setMinAge(faker.number().numberBetween(0, 18));
            event.setMaxAge(faker.number().numberBetween(event.getMinAge(), 19));
            event.setStatus(faker.options().option(EventStatus.class));
            event.setCreatorId((long) faker.number().numberBetween(0, 100));
            Set<Tag> randomEventTags = new HashSet<>();
            int numberOfTags = faker.number().numberBetween(1, 4);
            for (int j = 0; j < numberOfTags; j++) {
                Tag randomTag = allTags.get(faker.number().numberBetween(0, allTags.size()));
                randomEventTags.add(randomTag);
            }
            event.setTags(randomEventTags);
            try {
                eventService.create(event);
            } catch (Exception e) {
                System.out.println("Failed to save event: " + e.getMessage());
            }
        }
    }

    public void seedEventParticipationTable() {
        if (!eventParticipationService.getAll().isEmpty()) {
            return;
        }
        List<Event> allEvents = eventService.getAll(Pageable.unpaged()).getContent();
        if (allEvents.isEmpty()) {
            return;
        }
        for (int i = 0; i < 25; i++) {
            EventParticipation eventParticipation = new EventParticipation();
            eventParticipation.setUserId((long) faker.number().numberBetween(0, 100));
            eventParticipation.setEvent(allEvents.get(faker.number().numberBetween(0, allEvents.size())));
            eventParticipation.setJoinedAt(LocalDateTime.of(2026, 6, faker.number().numberBetween(1, 30), 12, 0));
            try {
                eventParticipationService.create(eventParticipation);
            } catch (Exception e) {
                System.out.println("Failed to save eventParticipation: " + e.getMessage());
            }
        }
    }

    public void seedFlagTable() {
        if (!flagService.getAll(Pageable.unpaged()).isEmpty()) {
            return;
        }
        List<Event> allEvents = eventService.getAll(Pageable.unpaged()).getContent();
        if (allEvents.isEmpty()) {
            return;
        }
        for (int i = 0; i < 25; i++) {
            Flag flag = new Flag();
            flag.setUserId((long) faker.number().numberBetween(0, 100));
            flag.setEvent(allEvents.get(faker.number().numberBetween(0, allEvents.size())));
            flag.setCreatedAt(LocalDateTime.of(2026, 7, faker.number().numberBetween(1, 30), 12, 0));
            flag.setReason(faker.harryPotter().quote());
            try {
                flagService.create(flag);
            } catch (Exception e) {
                System.out.println("Failed to save Flag: " + e.getMessage());
            }
        }
    }

    public void seedReviewTable() {
        if (!reviewService.getAll().isEmpty()) {
            return;
        }
        List<Event> allEvents = eventService.getAll(Pageable.unpaged()).getContent();
        List<User> allUsers = userRepository.findAll();
        if (allEvents.isEmpty() || allUsers.isEmpty()) {
            return;
        }
        for (int i = 0; i < 25; i++) {
            Review review = new Review();
            review.setUser(allUsers.get(faker.number().numberBetween(0, allUsers.size())));
            review.setEvent(allEvents.get(faker.number().numberBetween(0, allEvents.size())));
            review.setCreatedAt(LocalDateTime.of(2026, 7, faker.number().numberBetween(1, 30), 12, 0));
            review.setRating(faker.number().numberBetween(1, 6));
            review.setComment(faker.leagueOfLegends().quote());
            try {
                reviewService.create(review);
            } catch (Exception e) {
                System.out.println("Failed to save Review: " + e.getMessage());
            }
        }
    }

}
