package com.hivetech.kanban.dto;

public class UpdateUserRequest {
    private String email;
    private String password;
    private String currentPassword;
    private String avatarColor;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getCurrentPassword() { return currentPassword; }
    public String getAvatarColor() { return avatarColor; }
}
