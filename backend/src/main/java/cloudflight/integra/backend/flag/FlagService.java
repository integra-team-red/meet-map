package cloudflight.integra.backend.flag;

import cloudflight.integra.backend.event.EventService;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.flag.model.Flag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FlagService {
    private final FlagRepository repository;
    private final EventService eventService;

    public FlagService(FlagRepository repository, EventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
    }

    public List<Flag> getAll() {return repository.findAll();}

    public Optional<Flag> getById(Long id) { return repository.findById(id); }

    public Flag create(Flag flag) {
        Event event = eventService.getById(flag.getEvent().getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not found"));
        return repository.save(flag);
    }

    public Optional<Flag> update(Long id, Flag flag) {
        return repository.findById(id).map( existing -> {
            flag.setId(id);
            return repository.save(flag);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map( existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }
}
