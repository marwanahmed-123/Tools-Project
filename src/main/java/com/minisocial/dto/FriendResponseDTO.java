package com.minisocial.dto;

import java.util.List;

public class FriendResponseDTO {
    private String message;
    private List<FriendRequestDTO> requests;
    public FriendResponseDTO(String message, List<FriendRequestDTO> requests) {
        this.message = message;
        this.requests = requests;
    }
    public String getMessage() {
        return message;
    }
    public List<FriendRequestDTO> getRequests() {
        return requests;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setRequests(List<FriendRequestDTO> requests) {
        this.requests = requests;
    }
}