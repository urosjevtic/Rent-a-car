package services;

import beans.CancelTracker;
import beans.User;
import beans.enums.Role;
import dao.CancelTrackerDAO;
import dao.OrderDAO;
import dao.UserDAO;
import dto.UserDTO;
import dto.UserSearchDTO;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/profile")
public class ProfileService {
    @Context
    HttpServletRequest request;
    @Context
    ServletContext ctx;



    @GET
    @Path("/getProfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserData()
    {
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user == null) Response.status(Response.Status.FORBIDDEN).build();
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        return Response.ok(dao.getById(user.getUserId())).build();
    }

    @GET
    @Path("/getAllProfiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll()
    {
        User user = (User) request.getSession().getAttribute("loggedUser");

        if(!user.getRole().equals(Role.Administrator))
        {
            return Response.status(400).entity("Not an administrator").build();
        }

        CancelTrackerDAO trackerDAO = (CancelTrackerDAO) ctx.getAttribute("trackerDAO");
        UserDAO ud = (UserDAO) ctx.getAttribute("userDAO");
        List<User> users = ud.getAll();
        List<UserDTO> userDTOS = new ArrayList<UserDTO>();
        for(User u : users)
        {
            UserDTO userDTO = new UserDTO();
            userDTO.username = u.getUsername();
            userDTO.name = u.getName();
            userDTO.lastName = u.getLastName();
            userDTO.dateOfBirth = u.getBirthDate();
            userDTO.gender = u.getGender();
            userDTO.role = u.getRole();
            userDTO.isBlocked = u.getIsBlocked();
            userDTO.customerType = u.getCustomerType();
            userDTO.points = u.getPoints();

            CancelTracker tracker = trackerDAO.getByUserId(u.getUserId());

            userDTO.isSus = tracker != null && (tracker.getCanceled() > 5);

            userDTOS.add(userDTO);
        }
        return Response.status(Response.Status.ACCEPTED).entity(userDTOS).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(User newUserInfo)
    {
        User user = (User) request.getSession().getAttribute("loggedUser");

        if(user == null || !user.getUserId().equals(newUserInfo.getUserId())) return Response.status(Response.Status.FORBIDDEN).build();


        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        User updUser = dao.update(newUserInfo);
        if(updUser == null) return Response.status(Response.Status.BAD_REQUEST).build();
        return Response.ok(updUser).build();

    }





}
