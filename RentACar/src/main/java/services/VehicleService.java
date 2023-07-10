package services;

import beans.*;
import beans.enums.FuelType;
import beans.enums.OrderStatus;
import beans.enums.VehicleType;
import dao.OrderDAO;
import dao.RentACarObjectDAO;
import dao.ShopingCartDAO;
import dao.VehicleDAO;
import dto.ObjectDTO;
import dto.RentDateDTO;
import dto.RentVehicleDTO;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/vehicle")
public class VehicleService {

    @Context
    ServletContext ctx;
    @Context
    HttpServletRequest request;

    public VehicleService(){}



    @GET
    @Path("/getById")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Vehicle> getByObjectId(@QueryParam("id") String id){

        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        return dao.getByObjectId(id);
    }

    @GET
    @Path("/objByFuelType")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ObjectDTO> getObjectsByFuelType(@QueryParam("fuelType") FuelType fuelType){
        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        return dao.getObjectsWithFuelType(fuelType);
    }

    @GET
    @Path("/objByVehicleType")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ObjectDTO> getObjectsByFuelType(@QueryParam("vehicleType") VehicleType vehicleType){
        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        return dao.getObjectsWithVehicleType(vehicleType);
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(Vehicle vehicle){

        User manager = (User) request.getSession().getAttribute("loggedUser");
        if(manager == null || !vehicle.getCompanyId().equals(manager.getRentACarObjectId()))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        if(dao.save(vehicle) != null) return Response.ok().build();
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(Vehicle vehicle){

        User manager = (User) request.getSession().getAttribute("loggedUser");
        if(manager == null || !vehicle.getCompanyId().equals(manager.getRentACarObjectId()))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");

        if(dao.deleteVehicle(vehicle)) return Response.ok().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(Vehicle vehicle){

        User manager = (User) request.getSession().getAttribute("loggedUser");
        if(manager == null || !vehicle.getCompanyId().equals(manager.getRentACarObjectId()))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");

        if(dao.updateVehicle(vehicle)) return Response.ok().build();
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @GET
    @Path("/getAllAvailable")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Vehicle> getAllAvailable()
    {
        VehicleDAO dao = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        return dao.getAllAvailable();
    }

    @GET
    @Path("/getVehicales")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RentVehicleDTO> getVehicales()
    {
        RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        List<RentVehicleDTO> vehiclesForRental = new ArrayList<RentVehicleDTO>();
        for(Vehicle v : vehicleDAO.getAll())
        {
            RentVehicleDTO vehicleDTO = new RentVehicleDTO();
            vehicleDTO.id = v.getId();
            vehicleDTO.description = v.getDescription();
            vehicleDTO.fuelConsumption = v.getFuelConsumption();
            vehicleDTO.fuelType = v.getFuelType();
            vehicleDTO.make = v.getMake();
            vehicleDTO.vehicleType = v.getVehicleType();
            vehicleDTO.image = v.getImage();
            vehicleDTO.model = v.getModel();
            vehicleDTO.numberOfDoors = v.getNumberOfDoors();
            vehicleDTO.transmissionType = v.getTransmissionType();
            vehicleDTO.seatNumber = v.getSeatNumber();
            vehicleDTO.price = v.getPrice();
            vehicleDTO.objectName = objectDAO.getById(v.getCompanyId()).getName();
            vehicleDTO.objectLogo = objectDAO.getById(v.getCompanyId()).getImage();

            vehiclesForRental.add(vehicleDTO);
        }

        return vehiclesForRental;
    }

    @POST
    @Path("/getAvailable/{objectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<RentVehicleDTO> getByDateAvailability(@PathParam("objectId") String objectId, RentDateDTO rentDateDTO)
    {

        RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        List<RentVehicleDTO> vehiclesForRental = new ArrayList<RentVehicleDTO>();

        if(rentDateDTO.rentStartDate.getTime() > rentDateDTO.rentEndDate.getTime())
        {
            return vehiclesForRental;
        }

        Collection<Vehicle> availableVehicles = getAllFromSelectedObject(objectId);


        removeUnavailableVehicles(availableVehicles, rentDateDTO);


        removeIfInCart(availableVehicles);

        for(Vehicle v : availableVehicles)
        {
            RentVehicleDTO vehicleDTO = new RentVehicleDTO();
            vehicleDTO.id = v.getId();
            vehicleDTO.description = v.getDescription();
            vehicleDTO.fuelConsumption = v.getFuelConsumption();
            vehicleDTO.fuelType = v.getFuelType();
            vehicleDTO.make = v.getMake();
            vehicleDTO.vehicleType = v.getVehicleType();
            vehicleDTO.image = v.getImage();
            vehicleDTO.model = v.getModel();
            vehicleDTO.numberOfDoors = v.getNumberOfDoors();
            vehicleDTO.transmissionType = v.getTransmissionType();
            vehicleDTO.seatNumber = v.getSeatNumber();
            vehicleDTO.price = v.getPrice();
            vehicleDTO.objectName = objectDAO.getById(v.getCompanyId()).getName();
            vehicleDTO.objectLogo = objectDAO.getById(v.getCompanyId()).getImage();

            vehiclesForRental.add(vehicleDTO);
        }

        return vehiclesForRental;
    }

    private boolean isAvailableInBetweenDates(RentDateDTO rentDateDTO, Order order) {
        return (rentDateDTO.rentStartDate.after(order.getRentalEndDate()) || rentDateDTO.rentStartDate.equals(order.getRentalEndDate()))
                || (rentDateDTO.rentEndDate.before(order.getRentalStartDate()) || rentDateDTO.rentEndDate.equals(order.getRentalStartDate()));
    }

    private boolean dateOverlaps(RentDateDTO rentDateDTO, Order order) {
        boolean startInsideOrder = (rentDateDTO.rentStartDate.after(order.getRentalStartDate()) || rentDateDTO.rentStartDate.equals(order.getRentalStartDate()))
                && rentDateDTO.rentStartDate.before(order.getRentalEndDate());
        boolean endInsideOrder = rentDateDTO.rentEndDate.after(order.getRentalStartDate())
                && (rentDateDTO.rentEndDate.before(order.getRentalEndDate()) || rentDateDTO.rentEndDate.equals(order.getRentalEndDate()));

        boolean orderStartInsideRent = (order.getRentalStartDate().after(rentDateDTO.rentStartDate) || order.getRentalStartDate().equals(rentDateDTO.rentStartDate))
                && order.getRentalStartDate().before(rentDateDTO.rentEndDate);
        boolean orderEndInsideRent = order.getRentalEndDate().after(rentDateDTO.rentStartDate)
                && (order.getRentalEndDate().before(rentDateDTO.rentEndDate) || order.getRentalEndDate().equals(rentDateDTO.rentEndDate));

        boolean fullyInsideRent = rentDateDTO.rentStartDate.before(order.getRentalStartDate()) && rentDateDTO.rentEndDate.after(order.getRentalEndDate());

        return startInsideOrder || endInsideOrder || orderStartInsideRent || orderEndInsideRent || fullyInsideRent;
    }

    private Collection<Vehicle> getAllFromSelectedObject(String objectId)
    {
        VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        Collection<Vehicle> allVehicles = vehicleDAO.getAll();
        Collection<Vehicle> availableVehicles = new ArrayList<>();
        for(Vehicle vehicle : allVehicles)
        {
            if(vehicle.getCompanyId().equals(objectId)){
                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }

    private void removeUnavailableVehicles(Collection<Vehicle> availableVehicles, RentDateDTO rentDateDTO)
    {
        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        Collection<Order> orders = orderDAO.getAll();
        Collection<Vehicle> vehiclesToRemove = new ArrayList<Vehicle>();
        for(Order order : orders)
        {
            if(dateOverlaps(rentDateDTO, order))
            {
                for(Vehicle availableVehicle : availableVehicles)
                {
                    if(order.getVehiclesId().contains(availableVehicle.getId()) && (order.getStatus().equals(OrderStatus.Approved) || order.getStatus().equals(OrderStatus.Taken)))
                    {
                        vehiclesToRemove.add(availableVehicle);
                    }
                }
                availableVehicles.removeAll(vehiclesToRemove);
            }
        }
    }

    private void removeIfInCart(Collection<Vehicle> availableVehicles)
    {
        User user = (User) request.getSession().getAttribute("loggedUser");
        ShopingCartDAO cartDAO = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");

        ShopingCart cart = cartDAO.getByUserId(user.getUserId());

        Collection<Vehicle> vehiclesToRemove = new ArrayList<Vehicle>();

        for(Vehicle vehicle : availableVehicles)
        {
            if(cart.getVehicleIds().contains(vehicle.getId()))
            {
                vehiclesToRemove.add(vehicle);
            }
        }

        availableVehicles.removeAll(vehiclesToRemove);
    }

}
