package com.example.carly.mapper;

import com.example.carly.dto.auth.AuthUserDto;
import com.example.carly.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AuthUserDto toAuthUserDto(User user) {
        return new AuthUserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}