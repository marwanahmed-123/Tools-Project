package com.minisocial.dto;

import java.time.LocalDateTime;

public class PostInfoDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private UserInfoDTO author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int commentCount;

    public PostInfoDTO() {}
    public PostInfoDTO(Long id, String content, String imageUrl, UserInfoDTO author, LocalDateTime createdAt, LocalDateTime updatedAt, int likeCount, int commentCount) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
    public Long getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public UserInfoDTO getAuthor() {
        return author;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public int getCommentCount() {
        return commentCount;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setAuthor(UserInfoDTO author) {
        this.author = author;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}