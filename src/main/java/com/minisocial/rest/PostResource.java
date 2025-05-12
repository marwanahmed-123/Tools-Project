package com.minisocial.rest;
import com.minisocial.dto.CommentDTO;
import com.minisocial.dto.CommentInfoDTO;
import com.minisocial.dto.PostDTO;
import com.minisocial.dto.PostInfoDTO;
import com.minisocial.ejb.PostService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;
@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {
    @Inject
    PostService postService;

    @POST
    public Response createPost(@Valid PostDTO dto, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        PostInfoDTO post = postService.createPost(dto, userId);
        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    @Path("/{postId}")
    public Response getPost(@PathParam("postId") Long postId) {
        PostInfoDTO post = postService.getPostById(postId);
        return Response.ok(post).build();
    }

    @GET
    public Response getFeed(@Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        List<PostInfoDTO> feed = postService.getFeed(userId);
        return Response.ok(feed).build();
    }

    @PUT
    @Path("/{postId}")
    public Response updatePost(@PathParam("postId") Long postId, @Valid PostDTO dto, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            postService.updatePost(postId, dto, userId);
            return Response.ok("{\"message\": \"Post updated\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{postId}")
    public Response deletePost(@PathParam("postId") Long postId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            postService.deletePost(postId, userId);
            return Response.ok("{\"message\": \"Post deleted\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/{postId}/like")
    public Response likePost(@PathParam("postId") Long postId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            boolean liked = postService.toggleLike(postId, userId);
            String message = liked ? "Post liked" : "Post unliked";
            return Response.ok("{\"message\": \"" + message + "\"}").build();
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
    @POST
    @Path("/{postId}/comment")
    public Response commentOnPost(@PathParam("postId") Long postId, @Valid CommentDTO dto, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        postService.addComment(postId, dto, userId);
        return Response.status(Response.Status.CREATED).entity("{\"message\": \"Comment added\"}").build();
    }
    @GET
    @Path("/{postId}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@PathParam("postId") Long postId, @Context HttpServletRequest req) {
        Long userId = (Long) req.getSession(false).getAttribute("userId");
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Not logged in\"}")
                    .build();
        }
        try {
            List<CommentInfoDTO> comments = postService.getCommentsForPost(postId);
            return Response.ok(comments).build();
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
}