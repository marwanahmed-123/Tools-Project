package com.minisocial.rest;

import com.minisocial.dto.GroupDTO;
import com.minisocial.dto.GroupInfoDTO;
import com.minisocial.dto.GroupMemberDTO;
import com.minisocial.ejb.GroupService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource {
    @Inject
    GroupService groupService;

    @POST
    public Response createGroup(@Valid GroupDTO dto, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }

        try {
            GroupInfoDTO group = groupService.createGroup(dto, userId);
            return Response.status(Response.Status.CREATED).entity(group).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    public Response getAllGroups(@Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }

        try {
            List<GroupInfoDTO> groups = groupService.getGroups(userId);
            return Response.ok(groups).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred.\"}")
                    .build();
        }
    }
    @GET
    @Path("/{groupId}")
    public Response getGroup(@PathParam("groupId") Long groupId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }

        try {
            GroupInfoDTO group = groupService.getGroupById(groupId);
            return Response.ok(group).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Group not found\"}")
                    .build();
        }
    }
    @POST
    @Path("/{groupId}/join")
    public Response joinGroup(@PathParam("groupId") Long groupId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            groupService.joinGroup(groupId, userId);
            return Response.ok("{\"message\": \"Request submitted\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @POST
    @Path("/{groupId}/approve/{memberId}")
    public Response approveMember(@PathParam("groupId") Long groupId, @PathParam("memberId") Long memberId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }

        try {
            groupService.approveMember(groupId, memberId, userId);
            return Response.ok("{\"message\": \"Member approved\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @POST
    @Path("/{groupId}/reject/{memberId}")
    public Response rejectMember(
            @PathParam("groupId") Long groupId,
            @PathParam("memberId") Long memberId,
            @Context HttpServletRequest req) {

        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }

        try {
            groupService.rejectMember(groupId, memberId, userId);
            return Response.ok("{\"message\": \"Request rejected\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @POST
    @Path("/{groupId}/leave")
    public Response leaveGroup(@PathParam("groupId") Long groupId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            groupService.leaveGroup(groupId, userId);
            return Response.ok("{\"message\": \"Left group\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @DELETE
    @Path("/{groupId}")
    public Response deleteGroup(@PathParam("groupId") Long groupId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }

        try {
            groupService.deleteGroup(groupId, userId);
            return Response.ok("{\"message\": \"Group deleted\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @GET
    @Path("/{groupId}/members")
    public Response getMembers(@PathParam("groupId") Long groupId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            List<GroupMemberDTO> members = groupService.getMembers(groupId);
            return Response.ok(members).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}