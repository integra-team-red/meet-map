package cloudflight.integra.backend.flag;


import cloudflight.integra.backend.flag.model.CreateFlagDto;
import cloudflight.integra.backend.flag.model.Flag;
import cloudflight.integra.backend.flag.model.FlagDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/admin/flags", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get flags",
        operationId = "getFlags"
    )
    public Page<FlagDto> getAll(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable,
        @RequestParam(required = false) Long eventId
    ) {
        if(eventId != null) return service.getByEvent(pageable, eventId).map(mapper::toDto);
        return service.getAll(pageable).map(mapper::toDto);
    }

    @DeleteMapping(value = "/admin/flags/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
