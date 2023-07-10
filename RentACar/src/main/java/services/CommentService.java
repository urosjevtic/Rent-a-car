package services;

import beans.Comment;
import beans.Order;
import beans.User;
import beans.enums.CommentStatus;
import beans.enums.Role;
import dao.CommentDAO;
import dao.OrderDAO;
import dao.RentACarObjectDAO;
import dao.UserDAO;
import dto.SaveCommentDTO;
import dto.UpdateCommentDTO;
import dto.UserAndCommentDTO;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

@Path("/comment")
public class CommentService {

    @Context
    HttpServletRequest request;

    @Context
    ServletContext ctx;

    public CommentService(){}



    @GET
    @Path("/getById")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getByObjectId(@QueryParam("id") String id){

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.getByObjectId(id);
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(SaveCommentDTO commentDTO){

        Comment comment = new Comment();
        comment.setComment(commentDTO.comment);
        comment.setReviewScore(commentDTO.reviewScore);
        comment.setCommentStatus(CommentStatus.Pending);


        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        Order order = orderDAO.getByUniqueID(commentDTO.uniqueId);

        if(order == null) return Response.status(Response.Status.NOT_FOUND).build();

        comment.setCustomerId(order.getCustomerId());
        comment.setCompanyId(order.getObjectId());

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");

        if(dao.save(comment)){
            RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");

            if(objectDAO.updateRating(order.getObjectId(),dao.getRatingForObject(order.getObjectId())))
                return Response.ok().build();

        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

    @GET
    @Path("/getPending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingComments(@QueryParam("id")String objectId)
    {
        if(isInvalidManager(objectId)) return Response.status(Response.Status.FORBIDDEN).build();

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        Collection<Comment> comments = dao.getPendingComments(objectId);
        if(comments != null) return Response.ok(getAssembledComments(comments)).build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("/getAccepted")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAcceptedComments(@QueryParam("id")String objectId)
    {

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        Collection<Comment> comments = dao.getAcceptedComments(objectId);
        if(comments != null) return Response.ok(getAssembledComments(comments)).build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("/getDenied")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeniedComments(@QueryParam("id")String objectId)
    {

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        Collection<Comment> comments = dao.getDeniedComments(objectId);
        if(comments != null) return Response.ok(getAssembledComments(comments)).build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Path("/updateStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCommentStatus(UpdateCommentDTO updateCommentDTO)
    {
        if(isInvalidManager(updateCommentDTO.objectId)) return Response.status(Response.Status.FORBIDDEN).build();

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        if(dao.updateCommentStatus(updateCommentDTO))
        {
            if(updateCommentDTO.newCommentStatus.equals(CommentStatus.Accepted))
            {
                Collection<Comment> acceptedComments = dao.getAcceptedComments(updateCommentDTO.objectId);
                updateObjectRating(updateCommentDTO.objectId, acceptedComments);
            }
            return Response.ok().build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private boolean updateObjectRating(String objectId, Collection<Comment> acceptedComments)
    {
        RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        double ratingSum = 0;
        for (Comment comment : acceptedComments)
        {
            ratingSum += comment.getReviewScore();
        }
        return objectDAO.updateRating(objectId, ratingSum/acceptedComments.size());

    }

    @GET
    @Path("/getComments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDisplayComments(@QueryParam("id")String objectId){

        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        Collection<Comment> comments = dao.getAcceptedComments(objectId);

        if(comments == null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        if(isManagerOrAdmin()) comments.addAll(dao.getDeniedComments(objectId));

        return Response.ok(getAssembledComments(comments)).build();


    }



    private Collection<UserAndCommentDTO> getAssembledComments(Collection<Comment> comments){
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        Collection<UserAndCommentDTO> assembledComments = new ArrayList<UserAndCommentDTO>();

        for(Comment c: comments){
            UserAndCommentDTO assembledComment = new UserAndCommentDTO();
            assembledComment.comment = c;
            assembledComment.username = dao.getById(c.getCustomerId()).getUsername();
            assembledComments.add(assembledComment);
        }

        return assembledComments;
    }

    private boolean isInvalidManager(String objectId){
        User user = (User) request.getSession().getAttribute("loggedUser");
        return user.getRole() != Role.Manager || !user.getRentACarObjectId().equals(objectId);
    }

    private boolean isManagerOrAdmin(){
        User user = (User) request.getSession().getAttribute("loggedUser");
        return user!=null &&  (user.getRole() == Role.Manager || user.getRole() == Role.Administrator);
    }


}
