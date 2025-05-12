package com.minisocial.rest;

import com.minisocial.dto.*;
import com.minisocial.ejb.FriendService;
import com.minisocial.ejb.UserService;
import com.minisocial.entity.User;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;
    @Inject
    FriendService friendService;
    @POST
    @Path("/register")
    public Response register(@Valid UserDTO dto) {
        User registered = userService.registerUser(dto);
        return Response.status(Response.Status.CREATED).entity("{\"message\": \"User registered successfully.\"}").build();
    }

    @POST
    @Path("/login")
    public Response login(LoginDTO dto, @Context HttpServletRequest request) {
        User user = userService.findByEmail(dto.getEmail());
        if (user == null || !user.getPassword().equals(dto.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"Invalid credentials\"}")
                .build();
        }
        // Store user in session
        request.getSession().setAttribute("userId", user.getId());
        return Response.ok("{\"message\": \"Login successful.\", \"token\": \"FAKE-JWT-TOKEN\"}").build();
    }
    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); //destroy session
        }
        return Response.ok("{\"message\": \"Logged out successfully.\"}").build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<UserInfoDTO> users = userService.getAllUsers();
        return Response.ok(users).build();
    }
    @PUT
    @Path("/{userId}/update")
    public Response updateProfile(
        @PathParam("userId") Long userId,
        UpdateProfileDTO dto,
        @Context HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in.\"}")
                    .build();
            }
            Long sessionId = (Long) session.getAttribute("userId");
            if (!sessionId.equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"You can only update your own profile.\"}")
                    .build();
            }
            User updated = userService.updateProfile(userId, dto);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"User not found\"}").build();
            }
            return Response.ok("{\"message\": \"Profile updated successfully.\"}").build();
        }
    @GET
    @Path("/friends")
    public Response getFriends(@Context HttpServletRequest request) {
        Long userId = (Long) request.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in.\"}")
                    .build();
        }

        List<UserInfoDTO> friends = friendService.getFriends(userId);
        return Response.ok(friends).build();
    }

}