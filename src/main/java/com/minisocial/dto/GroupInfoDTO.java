package com.minisocial.dto;

public class GroupInfoDTO {
    private Long id;
    private String name;
    private String description;
    private UserInfoDTO admin;
    private boolean isOpen;
    private int memberCount;
    public GroupInfoDTO() {}
    public GroupInfoDTO(Long id, String name, String description, UserInfoDTO admin, boolean isOpen, int memberCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.isOpen = isOpen;
        this.memberCount = memberCount;
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public UserInfoDTO getAdmin() {
        return admin;
    }
    public boolean isOpen() {
        return isOpen;
    }
    public int getMemberCount() {
        return memberCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setAdmin(UserInfoDTO admin) {
        this.admin = admin;
    }
    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setIsOpen(boolean open) {
        isOpen = open;
    }
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
