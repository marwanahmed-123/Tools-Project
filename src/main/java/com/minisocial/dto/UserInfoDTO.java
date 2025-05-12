package com.minisocial.dto;

public class UserInfoDTO {
    private String email;
    private String name;
    private String bio;
    private String role;
    private Long id;
    public UserInfoDTO(String email, String name, String bio, String role, Long id) {
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.role = role;
        this.id = id;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }
    public String getRole() {
        return role;
    }
}