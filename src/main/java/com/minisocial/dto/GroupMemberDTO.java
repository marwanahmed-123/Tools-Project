package com.minisocial.dto;

public class GroupMemberDTO {
    private Long userId;
    private String name;
    private String role;
    private String status;
    public GroupMemberDTO() {}
    public GroupMemberDTO(Long userId, String name, String role, String status) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.status = status;
    }
    public Long getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getRole() {
        return role;
    }
    public String getStatus() {
        return status;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}