package cloudflight.integra.backend.geocoding;

import cloudflight.integra.backend.geocoding.model.Coordinate;
import cloudflight.integra.backend.geocoding.model.CoordinateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoordinateMapper {
    CoordinateDto toDto(Coordinate coordinate);
    Coordinate toEntity(CoordinateDto coordinateDto);
}
