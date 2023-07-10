package services;

import beans.ShopingCart;
import beans.Vehicle;
import beans.User;
import beans.enums.CustomerType;
import beans.enums.Role;
import dao.RentACarObjectDAO;
import dao.ShopingCartDAO;
import dao.VehicleDAO;
import dto.CartOrderDTO;
import dto.RentDateDTO;
import dto.RentVehicleDTO;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/shopingCart")
public class ShopingCartService {
    @Context
    ServletContext ctx;
    @Context
    HttpServletRequest request;




    @POST
    @Path("/addItemToCart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItem(RentVehicleDTO vehicle)
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user.getRole().equals(Role.Customer) && user != null)
        {
            ShopingCart shopingCart = dao.getByUserId(user.getUserId());
            List<String> vehicles = shopingCart.getVehicleIds();
            vehicles.add(vehicle.id);
            shopingCart.setVehicleIds(vehicles);
            shopingCart.setPrice(shopingCart.getPrice() + vehicle.price);
            dao.update(shopingCart);
            return Response.status(Response.Status.ACCEPTED).entity("Added to cart").build();
        }else {
            return Response.status(Response.Status.FORBIDDEN).entity("Not a customer").build();
        }

    }

    @GET
    @Path("/getCurrentPrice")
    @Produces(MediaType.APPLICATION_JSON)
    public double getCurrentPrice()
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        double price = dao.getCurrenPriceForUser(user.getUserId());
        if(user.getCustomerType().equals(CustomerType.Bronze))
        {
            price -= price*0.02;
        } else if (user.getCustomerType().equals(CustomerType.Silver)) {
            price -= price*0.05;
        } else if (user.getCustomerType().equals(CustomerType.Gold)) {
            price -= price*0.1;
        }
        return price;
    }

    @POST
    @Path("/setOrderData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOrderData(CartOrderDTO cartOrderDTO)
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user.getRole().equals(Role.Customer) && user != null)
        {
            if(dao.setCurrenOrderDateForUser(user.getUserId(), cartOrderDTO))
            {
                return Response.status(Response.Status.ACCEPTED).entity("Order data set").build();
            }else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Order data can't be set").build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).entity("Not a valid user").build();
    }

    @GET
    @Path("/getOrderDate")
    @Produces(MediaType.APPLICATION_JSON)
    public RentDateDTO getOrderDate()
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user.getRole().equals(Role.Customer) && user != null){
            return dao.getCurrenOrderDateForUser(user.getUserId());
        }
        return null;
    }

    @GET
    @Path("/getItems")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RentVehicleDTO> getCartItems()
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");

        if(user.getRole().equals(Role.Customer) && user != null)
        {
            List<RentVehicleDTO> vehiclesInCart = new ArrayList<>();

            ShopingCart shopingCart = dao.getByUserId(user.getUserId());
            for(String vehicleId : shopingCart.getVehicleIds())
            {
                Vehicle vehicle = vehicleDAO.getById(vehicleId);
                RentVehicleDTO vehicleDTO = new RentVehicleDTO();
                vehicleDTO.id = vehicle.getId();
                vehicleDTO.description = vehicle.getDescription();
                vehicleDTO.fuelConsumption = vehicle.getFuelConsumption();
                vehicleDTO.fuelType = vehicle.getFuelType();
                vehicleDTO.make = vehicle.getMake();
                vehicleDTO.vehicleType = vehicle.getVehicleType();
                vehicleDTO.image = vehicle.getImage();
                vehicleDTO.model = vehicle.getModel();
                vehicleDTO.numberOfDoors = vehicle.getNumberOfDoors();
                vehicleDTO.transmissionType = vehicle.getTransmissionType();
                vehicleDTO.seatNumber = vehicle.getSeatNumber();
                vehicleDTO.price = vehicle.getPrice();
                vehicleDTO.objectName = objectDAO.getById(vehicle.getCompanyId()).getName();
                vehicleDTO.objectLogo = objectDAO.getById(vehicle.getCompanyId()).getImage();

                vehiclesInCart.add(vehicleDTO);
            }

            return vehiclesInCart;
        }
        return null;
    }

    @POST
    @Path("/removeItem")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeItem(RentVehicleDTO rentVehicleDTO)
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user.getRole().equals(Role.Customer) && user != null)
        {
            dao.removeItem(user.getUserId(), rentVehicleDTO);
            return Response.status(Response.Status.ACCEPTED).entity("Item has been removed").build();
        }else {
            return Response.status(Response.Status.FORBIDDEN).entity("Not valid user").build();
        }
    }

    @DELETE
    @Path("/emptyCart")
    public void emptyCart()
    {
        ShopingCartDAO dao = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        if(user.getRole().equals(Role.Customer) && user != null)
        {
            dao.empty(user.getUserId());
        }
    }
}
