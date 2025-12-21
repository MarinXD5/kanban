package com.hivetech.kanban.dto;

public record AuthResponse(
        String token,
        Long id,
        String email,
        String name,
        String avatarColor
) {


}
