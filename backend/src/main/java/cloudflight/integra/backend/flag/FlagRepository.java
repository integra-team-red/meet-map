package cloudflight.integra.backend.flag;


import cloudflight.integra.backend.flag.model.Flag;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FlagRepository {
    private final Map<Long, Flag> flags = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Flag> findAll() {return new ArrayList<>(flags.values()); }

    public Optional<Flag> findById(Long id) {return Optional.ofNullable(flags.get(id)); }

    public Flag save(Flag flag) {
        if (flag.getId() == null) {
            flag.setId(idGen.getAndIncrement());
        }
        flags.put(flag.getId(),flag);
        return flag;
    }

    public void deleteById(Long id) { flags.remove(id); }
}
