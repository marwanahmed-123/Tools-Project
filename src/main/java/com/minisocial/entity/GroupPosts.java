package com.minisocial.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "group_posts")
public class GroupPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    public Long getId(){
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getAuthor(){
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }
    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}