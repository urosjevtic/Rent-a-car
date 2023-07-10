package services;

import beans.RentACarObject;
import beans.User;
import beans.enums.Role;
import dao.RentACarObjectDAO;
import dto.CitiesDTO;
import dto.ObjectDTO;

import javax.annotation.PostConstruct;
import javax.print.attribute.standard.Media;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/object")
public class RentObjectService {


    @Context
    HttpServletRequest request;
    @Context
    ServletContext ctx;

    public RentObjectService()
    {}


    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RentACarObject> getAll(){
        RentACarObjectDAO dao = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        return dao.getAll();
    }

    @GET
    @Path("/getCities")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<CitiesDTO> getCities(){
        RentACarObjectDAO dao = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        return dao.getAllCities();
    }

    @GET
    @Path("/getById")
    @Produces(MediaType.APPLICATION_JSON)
    public RentACarObject getById(@QueryParam("id") String id)
    {
        RentACarObjectDAO dao = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        return dao.getById(id);
    }


    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Save(RentACarObject rentACarObject)
    {
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user != null && user.getRole() == Role.Manager) return Response.status(Response.Status.FORBIDDEN).build();

        RentACarObjectDAO dao = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        return Response.ok(dao.save(rentACarObject)).build();
    }



}
