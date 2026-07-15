package cloudflight.integra.backend.tag;

import cloudflight.integra.backend.tag.model.Category;
import cloudflight.integra.backend.tag.model.TagDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService service;
    private final TagMapper mapper;

    public TagController(TagMapper mapper, TagService service){
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping
    public List<TagDto> getAll(@RequestParam(required = false) String category) {
        if(category != null)
            return service.getByCategory(Category.valueOf(category)).stream().map(mapper::toDto).toList();
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<TagDto> create(@RequestBody TagDto tagDto) {
        try {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDto(service.create(mapper.toEntity(tagDto))));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
