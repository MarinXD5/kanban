package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.UpdateUserRequest;
import com.hivetech.kanban.entity.User;

import java.util.List;

public interface UserService {
    User updateUser(Long userId, UpdateUserRequest request);

    List<User> getAllUsers();
}
