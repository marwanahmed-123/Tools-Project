package com.minisocial.dto;

import com.minisocial.entity.RequestStatus;

public class FriendRequestDTO {
    private Long senderId;
    private Long receiverId;
    private RequestStatus status;
    public FriendRequestDTO() {}
    public FriendRequestDTO(Long senderId, Long receiverId, RequestStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }
    public Long getSenderId() {
        return senderId;
    }
    public Long getReceiverId() {
        return receiverId;
    }
    public RequestStatus getStatus() {
        return status;
    }

    public void setSenderId(Long id) {
        this.senderId = id;
    }
    public void setReceiverId(Long id) {
        this.receiverId = id;
    }
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}