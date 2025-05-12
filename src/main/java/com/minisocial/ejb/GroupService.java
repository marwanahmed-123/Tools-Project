package com.minisocial.ejb;

import com.minisocial.dto.GroupInfoDTO;
import com.minisocial.dto.GroupDTO;
import com.minisocial.dto.GroupMemberDTO;
import com.minisocial.dto.UserInfoDTO;
import com.minisocial.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Stateless
public class GroupService {
    @PersistenceContext
    private EntityManager em;
    public GroupInfoDTO createGroup(GroupDTO dto, Long adminId) {
        User admin = em.find(User.class, adminId);
        if (admin == null) throw new IllegalArgumentException("Admin not found");
        Group group = new Group();
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setAdmin(admin);
        group.setIsOpen(dto.isOpen());
        group.setCreatedAt(LocalDateTime.now());
        em.persist(group);
        GroupMember member = new GroupMember();
        member.setUser(admin);
        member.setGroup(group);
        member.setStatus(MembershipStatus.APPROVED);
        member.setRole(Role.ADMIN);
        member.setJoinedAt(LocalDateTime.now());
        em.persist(member);
        return convertToDTO(group);
    }
    public List<GroupInfoDTO> getGroups(Long userId) {
        List<Group> groups = em.createQuery("SELECT g FROM Group g WHERE g.admin.id = :userId OR EXISTS(SELECT 1 FROM GroupMember gm WHERE gm.group.id = g.id AND gm.user.id = :userId AND gm.status = :approved)", Group.class)
                .setParameter("userId", userId)
                .setParameter("approved", MembershipStatus.APPROVED)
                .getResultList();
        return groups.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public GroupInfoDTO getGroupById(Long groupId) {
        Group group = em.find(Group.class, groupId);
        if (group == null) throw new IllegalArgumentException("Group not found");
        return convertToDTO(group);
    }
    public void joinGroup(Long groupId, Long userId) {
        Group group = em.find(Group.class, groupId);
        User user = em.find(User.class, userId);
        if (group == null || user == null) throw new IllegalArgumentException("Invalid group or user");
        GroupMember existing = getGroupMember(groupId, userId);
        if (existing != null) {
            if (existing.getStatus() == MembershipStatus.APPROVED) {
                throw new IllegalArgumentException("Already a member");
            } else if (existing.getStatus() == MembershipStatus.PENDING) {
                throw new IllegalArgumentException("Request already pending");
            }
        }
        GroupMember member = new GroupMember();
        member.setUser(user);
        member.setGroup(group);
        member.setStatus(group.isOpen() ? MembershipStatus.APPROVED : MembershipStatus.PENDING);
        member.setRole(Role.MEMBER);
        member.setJoinedAt(LocalDateTime.now());
        em.persist(member);
    }
    public void approveMember(Long groupId, Long memberId, Long adminId) {
        Group group = em.find(Group.class, groupId);
        User admin = em.find(User.class, adminId);
        GroupMember target = getGroupMember(groupId, memberId);
        if (group == null || admin == null || target == null) {
            throw new IllegalArgumentException("Invalid request");
        }
        if (!isAdmin(group, adminId)) {
            throw new IllegalArgumentException("You are not the group admin");
        }
        if (target.getStatus() != MembershipStatus.PENDING) {
            throw new IllegalArgumentException("User is not pending approval");
        }
        target.setStatus(MembershipStatus.APPROVED);
        em.merge(target);
    }
    public void rejectMember(Long groupId, Long memberId, Long adminId) {
        Group group = em.find(Group.class, groupId);
        User admin = em.find(User.class, adminId);
        GroupMember target = getGroupMember(groupId, memberId);
        if (group == null || admin == null || target == null) {
            throw new IllegalArgumentException("Invalid request");
        }
        if (!isAdmin(group, adminId)) {
            throw new IllegalArgumentException("You are not the group admin");
        }
        if (target.getStatus() != MembershipStatus.PENDING) {
            throw new IllegalArgumentException("User is not pending approval");
        }
        target.setStatus(MembershipStatus.REJECTED);
        em.merge(target);
    }
    public void leaveGroup(Long groupId, Long userId) {
        GroupMember member = getGroupMember(groupId, userId);
        if (member == null || member.getStatus() != MembershipStatus.APPROVED) {
            throw new IllegalArgumentException("Not a member");
        }
        em.remove(member);
    }
    public void deleteGroup(Long groupId, Long adminId) {
        Group group = em.find(Group.class, groupId);
        if (group == null) throw new IllegalArgumentException("Group not found");
        if (!group.getAdmin().getId().equals(adminId)) {
            throw new IllegalArgumentException("You are not the group admin");
        }
        List<GroupMember> members = em.createQuery("SELECT m FROM GroupMember m WHERE m.group.id = :groupId", GroupMember.class)
                .setParameter("groupId", groupId)
                .getResultList();
        members.forEach(em::remove);
        em.remove(group);
    }
    public List<GroupMemberDTO> getMembers(Long groupId) {
        List<GroupMember> members = em.createQuery("SELECT m FROM GroupMember m WHERE m.group.id = :groupId AND m.status = :approved", GroupMember.class)
                .setParameter("groupId", groupId)
                .setParameter("approved", MembershipStatus.APPROVED)
                .getResultList();
        return members.stream()
                .map(m -> new GroupMemberDTO(
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getRole().toString(),
                        m.getStatus().toString()))
                .collect(Collectors.toList());
    }
    public boolean isAdmin(Group group, Long userId) {
        return group.getAdmin().getId().equals(userId);
    }
    public GroupMember getGroupMember(Long groupId, Long userId) {
        return em.createQuery("SELECT m FROM GroupMember m WHERE m.group.id = :groupId AND m.user.id = :userId", GroupMember.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    private GroupInfoDTO convertToDTO(Group group) {
        GroupInfoDTO dto = new GroupInfoDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setAdmin(new UserInfoDTO(
                group.getAdmin().getEmail(),
                group.getAdmin().getName(),
                group.getAdmin().getBio(),
                group.getAdmin().getRole().toString(),
                group.getAdmin().getId()
        ));
        dto.setIsOpen(group.isOpen());
        dto.setMemberCount(getMemberCount(group.getId()));
        return dto;
    }
    private int getMemberCount(Long groupId) {
        return em.createQuery("SELECT COUNT(m) FROM GroupMember m WHERE m.group.id = :groupId AND m.status = :approved", Long.class)
                .setParameter("groupId", groupId)
                .setParameter("approved", MembershipStatus.APPROVED)
                .getSingleResult()
                .intValue();
    }
}