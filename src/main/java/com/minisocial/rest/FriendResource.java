package com.minisocial.rest;
import com.minisocial.dto.FriendRequestDTO;
import com.minisocial.dto.FriendResponseDTO;
import com.minisocial.ejb.FriendService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
@Path("/friend-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendResource {
    @Inject
    FriendService friendService;

    @POST
    public Response sendRequest(@Valid FriendRequestDTO dto, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession().getAttribute("userId");
        friendService.sendFriendRequest(userId, dto.getReceiverId());
        return Response.ok("{\"message\": \"Request sent\"}").build();
    }

    @PUT
    @Path("/{requestId}/accept")
    public Response acceptRequest(@PathParam("requestId") Long requestId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in.\"}")
                    .build();
        }
        try {
            FriendRequestDTO dto = friendService.acceptFriendRequest(requestId, userId);
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred.\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{senderId}/reject")
    public Response rejectRequest(@PathParam("senderId") Long senderId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in.\"}")
                    .build();
        }
        try {
            FriendRequestDTO dto = friendService.rejectFriendRequest(senderId, userId);
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred.\"}")
                    .build();
        }
    }

    @GET
    public Response getPendingRequests(@Context HttpServletRequest req) {
        Long userId = (Long) req.getSession().getAttribute("userId");
        List<FriendRequestDTO> requests = friendService.getPendingRequests(userId);
        return Response.ok(new FriendResponseDTO("Pending requests", requests)).build();
    }
    @DELETE
    @Path("/friends/{friendId}")
    public Response removeFriend(@PathParam("friendId") Long friendId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in.\"}")
                    .build();
        }

        try {
            friendService.removeFriend(userId, friendId);
            return Response.ok("{\"message\": \"Friend removed successfully\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
