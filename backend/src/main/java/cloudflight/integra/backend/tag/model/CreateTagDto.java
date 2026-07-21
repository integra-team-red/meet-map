package cloudflight.integra.backend.tag.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTagDto(
    @NotBlank
    @Size(max=255)
    String name,
    @NotNull
    Category category
) {
}
