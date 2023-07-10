package services;

import beans.*;
import beans.enums.CustomerType;
import beans.enums.OrderStatus;
import beans.enums.Role;
import dao.*;
import dto.CreateOrderDTO;
import dto.OrderDTO;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Path("/order")
public class OrderService {
    @Context
    ServletContext ctx;
    @Context
    HttpServletRequest request;



    @PUT
    @Path("/createOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder()
    {
        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        ShopingCartDAO shopingCartDAO = (ShopingCartDAO) ctx.getAttribute("shopingCartDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");

        ShopingCart cart = shopingCartDAO.getByUserId(user.getUserId());
        if(user.getUserId().equals(cart.getUserId()) && cart.getRentDateStart() != null && cart.getRentDateEnd() != null && cart.getObjectId() != null)
        {
            CreateOrderDTO createOrderDTO = new CreateOrderDTO();
            createOrderDTO.customerId = cart.getUserId();
            createOrderDTO.price = cart.getPrice();
            createOrderDTO.rentalStartDate = cart.getRentDateStart();
            createOrderDTO.rentalEndDate = cart.getRentDateEnd();
            createOrderDTO.objectId = cart.getObjectId();
            createOrderDTO.vehiclesId = cart.getVehicleIds();
            if(!createOrderDTO.isValid())
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Order can't be created").build();
            }

            if(user.getCustomerType().equals(CustomerType.Bronze))
            {
                createOrderDTO.price -= createOrderDTO.price*0.02;
            } else if (user.getCustomerType().equals(CustomerType.Silver)) {
                createOrderDTO.price -= createOrderDTO.price*0.05;
            } else if (user.getCustomerType().equals(CustomerType.Gold)) {
                createOrderDTO.price -= createOrderDTO.price*0.1;
            }

            Order newOrder = orderDAO.creatNewOrder(createOrderDTO);
            orderDAO.save(newOrder);

            addPointsToUser(user.getUserId(), newOrder.getPrice());

            return Response.status(Response.Status.ACCEPTED).entity("Order successfully created").build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Can't create order").build();
        }
    }



    private void addPointsToUser(String userId, double price)
    {
        int points = 5000;
        UserDAO userDAO = (UserDAO) ctx.getAttribute("userDAO");
        userDAO.modifyPoints(userId, points);
    }



    @GET
    @Path("/getCustomerOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderDTO> getByUserId()
    {
        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");
        CommentDAO commentDAO = (CommentDAO) ctx.getAttribute("commentDAO");

        Collection<Order> orders = orderDAO.getByCustomerId(user.getUserId());

        List<OrderDTO> userOrders = new ArrayList<>();

        for(Order order : orders)
        {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.uniqueId = order.getUniqueId();
            orderDTO.price = order.getPrice();
            orderDTO.rentalEndDate = order.getRentalEndDate();
            orderDTO.rentalStartDate = order.getRentalStartDate();
            orderDTO.status = order.getStatus();
            orderDTO.objectName = objectDAO.getById(order.getObjectId()).getName();
            orderDTO.reasonForDeclining = order.getRejectionReason();
            orderDTO.vehicleNames = new ArrayList<>();
            for(String id : order.getVehiclesId())
            {
                Vehicle vehicle = vehicleDAO.getById(id);
                StringBuilder vehicleName = new StringBuilder(vehicle.getMake());
                vehicleName.append(" ");
                vehicleName.append(vehicle.getModel());
                orderDTO.vehicleNames.add(vehicleName.toString());
            }
            orderDTO.isReviewable = orderDTO.status == OrderStatus.Returned && !commentDAO.commentExistsForUser(order.getCustomerId(),order.getObjectId());

            userOrders.add(orderDTO);
        }


        return userOrders;

    }

    @GET
    @Path("/getManagerOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderDTO> getByManagerId()
    {
        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        RentACarObjectDAO objectDAO = (RentACarObjectDAO) ctx.getAttribute("rentObjectDAO");

        Collection<Order> orders = orderDAO.getByObjectId(user.getRentACarObjectId());

        List<OrderDTO> userOrders = new ArrayList<>();

        for(Order order : orders)
        {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.uniqueId = order.getUniqueId();
            orderDTO.price = order.getPrice();
            orderDTO.rentalEndDate = order.getRentalEndDate();
            orderDTO.rentalStartDate = order.getRentalStartDate();
            orderDTO.status = order.getStatus();
            orderDTO.objectName = objectDAO.getById(order.getObjectId()).getName();
            orderDTO.reasonForDeclining = order.getRejectionReason();
            orderDTO.vehicleNames = new ArrayList<>();
            for(String id : order.getVehiclesId())
            {
                Vehicle vehicle = vehicleDAO.getById(id);
                StringBuilder vehicleName = new StringBuilder(vehicle.getMake());
                vehicleName.append(" ");
                vehicleName.append(vehicle.getModel());
                orderDTO.vehicleNames.add(vehicleName.toString());
            }

            userOrders.add(orderDTO);
        }


        return userOrders;

    }


    @POST
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cancelOrder(OrderDTO order)
    {
        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");
        Order o = orderDAO.getById(order.uniqueId);
        if(user.getUserId().equals(o.getCustomerId()))
        {
            if(order.status != OrderStatus.Processing)
            {
                return Response.status(Response.Status.FORBIDDEN).entity("Can't cancel is status is not processing").build();
            }
            orderDAO.cancel(order.uniqueId);
            removePointsFromUser(user.getUserId(), order.price);
            trackCancel(orderDAO.getByUniqueID(order.uniqueId));
            return Response.status(Response.Status.ACCEPTED).entity("Order canceled").build();
        }else {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong user").build();
        }
    }

    private void trackCancel(Order order){
        CancelTrackerDAO trackerDAO = (CancelTrackerDAO) ctx.getAttribute("trackerDAO");

        CancelTracker tracker = trackerDAO.getByUserId(order.getCustomerId());
        if(tracker == null){
            tracker = new CancelTracker(1,new Date(System.currentTimeMillis()),order.getCustomerId());
            trackerDAO.save(tracker);
        }else{

            Date lastCancel = tracker.getFirstCancel();
            LocalDateTime last = lastCancel.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime current = LocalDateTime.now();

            LocalDateTime oneMonthAhead = current.plus(1, ChronoUnit.MONTHS);

            if(last.isAfter(oneMonthAhead)){
                //more than a month away
                tracker.setCanceled( 0 );
                tracker.setFirstCancel( new Date(System.currentTimeMillis()) );
            }else{
                //withing a month
                tracker.setCanceled( tracker.getCanceled() + 1 );

            }

            trackerDAO.updateTracker(tracker);

        }


    }

    private void removePointsFromUser(String userId, double price)
    {
        int points = (int) (price/1000 * 133 * 4);
        UserDAO userDAO = (UserDAO) ctx.getAttribute("userDAO");
        userDAO.modifyPoints(userId, -points);
    }

    @POST
    @Path("/processOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processOrder(@QueryParam("orderId") String orderId, @QueryParam("action") String action, @QueryParam("rejectionReason") String rejectionReason) {
        OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
        User user = (User) request.getSession().getAttribute("loggedUser");

        if(user.getRole().equals(Role.Manager) && user != null)
        {
            if(orderDAO.changeOrderStatus(orderId, action, rejectionReason)){

                if(action.equals(OrderStatus.Taken.toString()))
                {
                    VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
                    for(String vehicleId : orderDAO.getById(orderId).getVehiclesId())
                    {
                        vehicleDAO.changeStatus(vehicleId, true);
                    }
                }

                if(action.equals(OrderStatus.Returned.toString()))
                {
                    VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
                    for(String vehicleId : orderDAO.getById(orderId).getVehiclesId())
                    {
                        vehicleDAO.changeStatus(vehicleId, false);
                    }
                }

                return Response.status(Response.Status.ACCEPTED).entity("Status changed").build();
            }

        }

        return Response.status(Response.Status.BAD_REQUEST).entity("Can't change status").build();

    }
}
