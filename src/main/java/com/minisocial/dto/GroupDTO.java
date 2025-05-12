package com.minisocial.dto;

public class GroupDTO {
    private String name;
    private String description;
    private boolean isOpen;
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public boolean isOpen() {
        return isOpen;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setOpen(boolean open) {
        isOpen = open;
    }
}