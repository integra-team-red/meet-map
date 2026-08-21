package cloudflight.integra.backend.user.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateUserTagsDto(
    @NotNull
    List<Long> tagIds
) {
}

