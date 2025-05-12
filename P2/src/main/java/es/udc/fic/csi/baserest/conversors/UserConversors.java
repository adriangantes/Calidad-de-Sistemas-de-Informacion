package es.udc.fic.csi.baserest.conversors;

import java.util.List;

import es.udc.fic.csi.baserest.dto.UserDto;
import es.udc.fic.csi.baserest.entity.User;

/**
 * Utility class with conversions between {@link User} and {@link UserDto}
 * 
 * @author anxo.pvila@udc.es
 */
public class UserConversors {

    /**
     * Private constructor to avoid instantiation
     */
    private UserConversors() {
    }

    /**
     * Convert to a {@link UserDto}
     * 
     * @param user the user entity
     * @return a user dto
     */
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getName(), user.getAge());
    }

    /**
     * Convert a {@link List} of {@link User} to a {@link List} of {@link UserDto}
     * 
     * @param users the list of user entities
     * @return a list of user dtos
     */
    public static List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(UserConversors::toUserDto).toList();
    }

    /**
     * Convert a {@link UserDto} to a {@link User} without id
     * 
     * @param user the user dto
     * @return a user entity without id
     */
    public static User toUser(UserDto user) {
        return new User(user.name(), user.age());
    }
}
