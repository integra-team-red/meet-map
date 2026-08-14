package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
