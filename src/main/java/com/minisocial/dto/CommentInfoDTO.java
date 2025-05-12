package com.minisocial.dto;

import java.time.LocalDateTime;

public class CommentInfoDTO {
    private Long id;
    private String content;
    private UserInfoDTO author;
    private LocalDateTime createdAt;

    public CommentInfoDTO(Long id, String content, UserInfoDTO author, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public UserInfoDTO getAuthor() {
        return author;
    }
}