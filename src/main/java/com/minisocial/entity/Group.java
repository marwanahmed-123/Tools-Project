package com.minisocial.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    private boolean isOpen; // Open = auto-join, Closed = require approval

    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public User getAdmin() {
        return admin;
    }
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(boolean open) {
        isOpen = open;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Group() {}
    public Group(String name, String description, User admin, boolean isOpen) {
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.isOpen = isOpen;
        this.createdAt = LocalDateTime.now();
    }
}