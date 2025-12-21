package com.hivetech.kanban.controller;

import com.hivetech.kanban.dto.UpdateUserRequest;
import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.service.ProjectServiceImpl;
import com.hivetech.kanban.service.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return userService.getAllUsers();
    }

    @PutMapping("/change/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        User user = userService.updateUser(id, request);
        return userService.updateUser(id, request);
    }
}