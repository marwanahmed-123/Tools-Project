package com.minisocial.ejb;

import com.minisocial.dto.*;
import com.minisocial.entity.Comment;
import com.minisocial.entity.Like;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// com/minisocial/ejb/PostService.java
@Stateless
public class PostService {
    @PersistenceContext
    private EntityManager em;

    @Inject
    UserService userService;

    @Inject
    FriendService friendService;

    public PostInfoDTO createPost(PostDTO dto, Long userId) {
        User user = em.find(User.class, userId);
        if (user == null) throw new IllegalArgumentException("User not found");

        Post post = new Post();
        post.setUser(user);
        post.setContent(dto.getContent());
        post.setImageUrl(dto.getImageUrl());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        em.persist(post);

        return convertToDTO(post);
    }

    public PostInfoDTO getPostById(Long postId) {
        Post post = em.find(Post.class, postId);
        if (post == null) throw new IllegalArgumentException("Post not found");
        return convertToDTO(post);
    }

    public List<PostInfoDTO> getFeed(Long userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<UserInfoDTO> friends = Optional.ofNullable(friendService.getFriends(userId))
                .orElse(Collections.emptyList());
        List<Long> friendIds = friends.stream()
                .map(UserInfoDTO::getId)
                .filter(id -> !id.equals(userId))
                .collect(Collectors.toList());
        friendIds.add(userId);
        try {
            List<Post> posts = em.createQuery("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC", Post.class)
                    .setParameter("userIds", friendIds)
                    .getResultList();
            return posts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving feed: " + e.getMessage(), e);
        }
    }

    public void updatePost(Long postId, PostDTO dto, Long userId) {
        Post post = em.find(Post.class, postId);
        if (post == null || !post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own posts");
        }
        post.setContent(dto.getContent());
        post.setImageUrl(dto.getImageUrl());
        post.setUpdatedAt(LocalDateTime.now());
        em.merge(post);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        if (post == null || !post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own posts");
        }
        em.remove(post);
    }

    public boolean addLike(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);

        if (post == null || user == null) {
            throw new IllegalArgumentException("Post or user not found");
        }
        // Check if user already liked the post
        Long count = (Long) em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
                .setParameter("userId", userId)
                .setParameter("postId", postId)
                .getSingleResult();

        if (count > 0) {
            return false;
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        like.setCreatedAt(LocalDateTime.now());

        em.persist(like);
        return true;
    }
    public boolean toggleLike(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);

        if (post == null || user == null) {
            throw new IllegalArgumentException("Post or user not found");
        }
        Like existingLike = em.createQuery("SELECT l FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId", Like.class)
                .setParameter("userId", userId)
                .setParameter("postId", postId)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (existingLike != null) {
            em.remove(existingLike); // Unlike
            return false;
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            like.setCreatedAt(LocalDateTime.now());
            em.persist(like);
            return true;
        }
    }

    public void addComment(Long postId, CommentDTO dto, Long userId) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);
        if (post == null || user == null) throw new IllegalArgumentException("Invalid post or user");

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        em.persist(comment);
    }

    private PostInfoDTO convertToDTO(Post post) {
        PostInfoDTO dto = new PostInfoDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setAuthor(new UserInfoDTO(
                post.getUser().getEmail(),
                post.getUser().getName(),
                post.getUser().getBio(),
                post.getUser().getRole().toString(),
                post.getUser().getId()
        ));
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLikeCount(countLikes(post.getId()));
        dto.setCommentCount(countComments(post.getId()));
        return dto;
    }

    private int countLikes(Long postId) {
        return em.createQuery("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult()
                .intValue();
    }

    private int countComments(Long postId) {
        return em.createQuery("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult()
                .intValue();
    }
    public List<CommentInfoDTO> getCommentsForPost(Long postId) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        List<Comment> comments = em.createQuery("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("postId", postId)
                .getResultList();

        return comments.stream()
                .map(c -> new CommentInfoDTO(
                        c.getId(),
                        c.getContent(),
                        new UserInfoDTO(
                                c.getUser().getEmail(),
                                c.getUser().getName(),
                                c.getUser().getBio(),
                                c.getUser().getRole().toString(),
                                c.getUser().getId()
                        ),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
