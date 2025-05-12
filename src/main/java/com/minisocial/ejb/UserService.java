package com.minisocial.ejb;

import com.minisocial.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import com.minisocial.dto.UpdateProfileDTO;
import com.minisocial.dto.UserDTO;
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
}