package cloudflight.integra.backend.flag;


import cloudflight.integra.backend.flag.model.CreateFlagDto;
import cloudflight.integra.backend.flag.model.Flag;
import cloudflight.integra.backend.flag.model.FlagDto;
import jakarta.validation.Valid;
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

    @PostMapping(value = "/flags")
    public ResponseEntity<FlagDto> create (
        @RequestParam Long userId,
        @Valid
        @RequestBody CreateFlagDto dto
        ) {
        Flag flag = mapper.toEntity(dto);
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
