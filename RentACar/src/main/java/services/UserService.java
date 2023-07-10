package services;

import beans.ShopingCart;
import beans.enums.Role;
import dao.ShopingCartDAO;
import dao.UserDAO;
import beans.User;
import dto.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/user")
public class UserService {

    @Context
    HttpServletRequest request;
    @Context
    ServletContext ctx;

    public UserService(){}


    @GET
    @Path("/freeManagers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFreeManagers()
    {
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user == null || user.getRole() != Role.Administrator) return Response.status(Response.Status.FORBIDDEN).build();

        UserDAO repo = (UserDAO) ctx.getAttribute("userDAO");
        return Response.ok(repo.getAllFreeManagers()).build();
    }

    @POST
    @Path("/updateObjectInManager")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateObjectInManager(UpdateManagerObjectDTO dto)
    {
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user == null || user.getRole() != Role.Administrator) return Response.status(Response.Status.FORBIDDEN).build();

        UserDAO repo = (UserDAO) ctx.getAttribute("userDAO");
        return Response.ok(repo.updateObjectInManager(dto)).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(User newUser)
    {
        System.out.println("Starting registration");
        if(!newUser.ValidateRegistrationData())
            return Response.status(Response.Status.BAD_REQUEST).entity("Data not valid").build();

        if(createNewUser(newUser))
        {
            return Response.status(Response.Status.ACCEPTED).entity("User successfully created").build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).entity("User couldn't be created").build();
        }
    }

    private boolean createNewUser(User newUser)
    {
        UserDAO repo = (UserDAO) ctx.getAttribute("userDAO");
        if((newUser = repo.save(newUser))==null){
            return false;
        }
        ShopingCartDAO shopingCartDAO = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        ShopingCart shopingCart = new ShopingCart();
        shopingCart.setUserId(newUser.getUserId());
        shopingCart.setPrice(0);
        shopingCart.setVehicleIds(new ArrayList<>());
        shopingCartDAO.save(shopingCart);
        return true;
    }
    @POST
    @Path("/registerManager")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerManager(User newManager)
    {

        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user == null || user.getRole() != Role.Administrator) return Response.status(Response.Status.FORBIDDEN).build();

        ManagersDTO returnData = new ManagersDTO();
        returnData.fullNameUsername = "";
        UserDAO repo = (UserDAO) ctx.getAttribute("userDAO");

        if(!newManager.ValidateRegistrationData())
        {
            returnData.id = "-1";
            return Response.ok(returnData).build();
        }
        User userData;
        if((userData = repo.save(newManager)) == null)
        {
            returnData.id = "-1";
            return Response.ok(returnData).build();
        }

        returnData.id = userData.getUserId();
        return Response.ok(returnData).build();

    }

    @GET
    @Path("/getManagerObjectID")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObjectIDForManager(){

        User loggedManager = (User) request.getSession().getAttribute("loggedUser");
        if(loggedManager == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        ObjectDTO objectID = new ObjectDTO();
        objectID.id = loggedManager.getRentACarObjectId();

        return Response.ok(objectID).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO loginDTO)
    {
        System.out.println("Starting loging");
        UserDAO ud = (UserDAO) ctx.getAttribute("userDAO");
        User foundUser = ud.getByUsername(loginDTO.username);
        if(!foundUser.getUsername().equals(loginDTO.username))
        {
            System.out.println("Wrong username");
            return Response.status(Response.Status.BAD_REQUEST).entity("Username is incorrect, try again").build();
        }
        if(!foundUser.getPassword().equals(loginDTO.password))
        {
            System.out.println("Wrong password");
            return Response.status(Response.Status.BAD_REQUEST).entity("Password is incorrect, try again").build();
        }
        if(foundUser.getIsBlocked())
        {
            System.out.println("User blocked");
            return Response.status(Response.Status.ACCEPTED).entity("User is blocked").build();
        }
        request.getSession().setAttribute("loggedUser", foundUser);
        System.out.println("New session given");
        if(foundUser.getRole().equals(Role.Customer))
            return Response.status(Response.Status.ACCEPTED).entity("/RentACar_war_exploded/customerScreen.html").build();
        else if(foundUser.getRole().equals(Role.Administrator))
            return Response.status(Response.Status.ACCEPTED).entity("/RentACar_war_exploded/administratorScreen.html").build();
        else if(foundUser.getRole().equals(Role.Manager))
            return Response.status(Response.Status.ACCEPTED).entity("/RentACar_war_exploded/managerScreen.html").build();
        else
            return Response.status(Response.Status.BAD_REQUEST).entity("error").build();
    }

    @DELETE
    @Path("/logout")
    public Response logout()
    {
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user!=null)
        {
            request.getSession().invalidate();
            return Response.status(Response.Status.ACCEPTED).entity("/RentACar_war_exploded/index.html").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("User nog logged in").build();
    }

    @POST
    @Path("/blockUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response blockUser(UserDTO user){
        System.out.println("Blocking user");
        UserDAO ud = (UserDAO) ctx.getAttribute("userDAO");
        User selectedUser = ud.getByUsername(user.username);
        if(selectedUser.getRole().equals(Role.Administrator))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cant't block administrator").build();
        }
        selectedUser.setIsBlocked(true);
        ud.update(selectedUser);
        return Response.status(Response.Status.ACCEPTED).entity("User blocked").build();
    }

    @POST
    @Path("/unblockUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void unblockUser(UserDTO user){
        System.out.println("Unblocking user");
        UserDAO ud = (UserDAO) ctx.getAttribute("userDAO");
        User selectedUser = ud.getByUsername(user.username);
        selectedUser.setIsBlocked(false);
        ud.update(selectedUser);
    }

}