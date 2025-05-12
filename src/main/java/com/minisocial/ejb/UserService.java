package com.minisocial.ejb;

import com.minisocial.dto.UserInfoDTO;
import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import com.minisocial.dto.UpdateProfileDTO;
import com.minisocial.dto.UserDTO;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public User registerUser(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Consider hashing later
        user.setName(dto.getName());
        user.setBio(dto.getBio());
        user.setRole(User.Role.valueOf(dto.getRole().toUpperCase()));
        em.persist(user);
        return user;
    }

    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    @Transactional
    public User updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = em.find(User.class, userId);
        if (user == null) return null;

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());

        return em.merge(user);
    }
    public List<UserInfoDTO> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class)
                .getResultList()
                .stream()
                .map(u -> new UserInfoDTO(
                        u.getEmail(),
                        u.getName(),
                        u.getBio(),
                        u.getRole().toString(),
                        u.getId()))
                .collect(toList());
    }

    public User findUserById(Long userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return user;
    }
}