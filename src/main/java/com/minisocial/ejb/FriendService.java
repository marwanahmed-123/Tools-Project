package com.minisocial.ejb;

import com.minisocial.dto.FriendRequestDTO;
import com.minisocial.dto.UserInfoDTO;
import com.minisocial.entity.FriendRequest;
import com.minisocial.entity.RequestStatus;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// com/minisocial/ejb/FriendService.java
@Stateless
public class FriendService {
    @PersistenceContext
    private EntityManager em;
    public void sendFriendRequest(Long senderId, Long receiverId) {
        User sender = em.find(User.class, senderId);
        User receiver = em.find(User.class, receiverId);
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send request to self");
        }
        FriendRequest existing = getExistingRequest(senderId, receiverId);
        if (existing != null) {
            if (existing.getStatus() == RequestStatus.PENDING) {
                throw new IllegalArgumentException("A pending request already exists");
            } else if (existing.getStatus() == RequestStatus.ACCEPTED) {
                throw new IllegalArgumentException("You are already friends");
            } else if (existing.getStatus() == RequestStatus.REJECTED) {
                throw new IllegalArgumentException("This request was previously rejected");
            }
        }
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        em.persist(request);
    }
    @Transactional
    public FriendRequestDTO acceptFriendRequest(Long requestId, Long userId) {
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request == null || request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Invalid or non-pending request");
        }
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to accept this request");
        }
        request.setStatus(RequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        em.merge(request);

        return convertToDTO(request);
    }
    @Transactional
    public FriendRequestDTO rejectFriendRequest(Long requestId, Long userId) {
        FriendRequest request = em.find(FriendRequest.class, requestId);
        if (request == null || request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Invalid or non-pending request");
        }
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to reject this request");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        em.merge(request);

        return convertToDTO(request);
    }
    private User findUserById(Long userId) {
        return em.find(User.class, userId);
    }
    private FriendRequest getExistingRequest(Long senderId, Long receiverId) {
        return em.createQuery(
                        "SELECT fr FROM FriendRequest fr " +
                                "WHERE (fr.sender.id = :senderId AND fr.receiver.id = :receiverId) " +
                                "OR (fr.sender.id = :receiverId AND fr.receiver.id = :senderId)",
                        FriendRequest.class)
                .setParameter("senderId", senderId)
                .setParameter("receiverId", receiverId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    public List<FriendRequestDTO> getPendingRequests(Long userId) {
        List<FriendRequest> pendingRequests = em.createQuery(
                        "SELECT fr FROM FriendRequest fr " +
                                "WHERE fr.receiver.id = :userId AND fr.status = :status " +
                                "ORDER BY fr.createdAt DESC",
                        FriendRequest.class)
                .setParameter("userId", userId)
                .setParameter("status", RequestStatus.PENDING)
                .getResultList();
        return pendingRequests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private FriendRequestDTO convertToDTO(FriendRequest fr) {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setSenderId(fr.getSender().getId());
        dto.setReceiverId(fr.getReceiver().getId());
        dto.setStatus(fr.getStatus());
        return dto;
    }
    public List<UserInfoDTO> getFriends(Long userId) {
        List<FriendRequest> acceptedRequests = em.createQuery(
                        "SELECT fr FROM FriendRequest fr " +
                                "WHERE (fr.sender.id = :userId OR fr.receiver.id = :userId) " +
                                "AND fr.status = :status",
                        FriendRequest.class
                )
                .setParameter("userId", userId)
                .setParameter("status", RequestStatus.ACCEPTED)
                .getResultList();

        return acceptedRequests.stream()
                .map(fr -> {
                    User otherUser;
                    if (fr.getSender().getId().equals(userId)) {
                        otherUser = fr.getReceiver();
                    } else {
                        otherUser = fr.getSender();
                    }
                    return new UserInfoDTO(
                            otherUser.getEmail(),
                            otherUser.getName(),
                            otherUser.getBio(),
                            otherUser.getRole().toString(),
                            otherUser.getId()
                    );
                })
                .distinct()
                .collect(Collectors.toList());
    }
    public void removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Cannot remove yourself from friends");
        }
        FriendRequest request = em.createQuery(
                        "SELECT fr FROM FriendRequest fr " +
                                "WHERE ((fr.sender.id = :userId AND fr.receiver.id = :friendId) " +
                                "OR (fr.sender.id = :friendId AND fr.receiver.id = :userId)) " +
                                "AND fr.status = :status",
                        FriendRequest.class)
                .setParameter("userId", userId)
                .setParameter("friendId", friendId)
                .setParameter("status", RequestStatus.ACCEPTED)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No active friendship found"));
        em.remove(request);
    }
}