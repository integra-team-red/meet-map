package cloudflight.integra.backend.flag;

import cloudflight.integra.backend.flag.model.Flag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlagService {
    private final FlagRepository repository;

    public FlagService(FlagRepository repository) {
        this.repository = repository;
    }

    public List<Flag> getAll() {return repository.findAll();}

    public Optional<Flag> getById(Long id) { return repository.findById(id); }

    public Flag create(Flag flag) {
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
