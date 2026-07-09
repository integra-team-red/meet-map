package cloudflight.integra.backend.flag;


import cloudflight.integra.backend.flag.model.Flag;
import cloudflight.integra.backend.flag.model.FlagDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FlagController {
    private final FlagService service;
    private final FlagMapper mapper;

    public FlagController(FlagService service, FlagMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping(value = "/events/{eventId}/flags")
    public ResponseEntity<FlagDto> create
        (
            @PathVariable Long eventId,
            @RequestParam Long userId,
            @RequestBody FlagDto dto
        ) {

        Flag flag = mapper.toEntity(dto);
        flag.setEventId(eventId);
        flag.setUserId(userId);

        Flag saved = service.create(flag);
        FlagDto flagDto = mapper.toDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(flagDto);
    }

    @GetMapping(value = "/admin/flags")
    public List<FlagDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }


    @DeleteMapping(value = "/admin/flags/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
