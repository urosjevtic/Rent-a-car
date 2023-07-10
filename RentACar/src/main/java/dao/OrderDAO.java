package dao;

import beans.Order;
import beans.enums.OrderStatus;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CreateOrderDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class OrderDAO {

    private HashMap<String, Order> orders;

    private final ObjectMapper objectMapper;
    private final File file;

    public OrderDAO(String CtxPath) {
        objectMapper = new ObjectMapper();
        orders = new HashMap<String, Order>();
        String filePath = CtxPath + "..\\..\\src\\main\\resources\\Orders.JSON";
        file = new File(filePath);

        createFile();
        readFromFileJSON();
    }

    private Integer nextId(){ return orders.keySet().size()+1;}
    public Order save(Order order)
    {
        order.setId(nextId().toString());
        orders.put(order.getUniqueId(),order);

        writeToFileJSON();
        readFromFileJSON();

        return order;

    }
    public Collection<Order> getAll()
    {
        return orders.values();
    }

    public Order getById(String uniqueId)
    {
        return orders.get(uniqueId);
    }
    public Collection<Order> getByCustomerId(String customerId)
    {
        readFromFileJSON();
        Collection<Order> customerOrders = new ArrayList<>();
        for(Order order : orders.values())
        {
            if(order.getCustomerId().equals(customerId) && !order.getIsDeleted())
            {
                customerOrders.add(order);
            }
        }
        return customerOrders;
    }

    public Collection<Order> getByObjectId(String objectId)
    {
        readFromFileJSON();
        Collection<Order> managerOrders = new ArrayList<>();
        for(Order order : orders.values())
        {
            if(order.getObjectId().equals(objectId) && !order.getIsDeleted())
            {
                managerOrders.add(order);
            }
        }
        return managerOrders;
    }

    public Order getByUniqueID(String uniqueId)
    {
        return orders.values().stream().filter(x -> x.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    public Order creatNewOrder(CreateOrderDTO createOrderDTO)
    {
        Order newOrder = new Order();
        newOrder.setUniqueId(generateUniqueId());
        newOrder.setCustomer(createOrderDTO.customerId);
        newOrder.setPrice(createOrderDTO.price);
        newOrder.setStatus(OrderStatus.Processing);
        newOrder.setObjectId(createOrderDTO.objectId);
        newOrder.setRentalStartDate(createOrderDTO.rentalStartDate);
        newOrder.setRentalEndDate(createOrderDTO.rentalEndDate);
        newOrder.setVehiclesId(createOrderDTO.vehiclesId);
        newOrder.setIsDeleted(false);

        return newOrder;

    }


    private String generateUniqueId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random random = new Random();
        int stringLength = 10;
        StringBuilder uniqueId = new StringBuilder(stringLength);

        for (int i = 0; i < stringLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            uniqueId.append(randomChar);
        }

        return uniqueId.toString();
    }

    public void cancel(String orderUniqueId)
    {
        for(Order order : orders.values())
        {
            if(order.getUniqueId().equals(orderUniqueId))
            {
                order.setStatus(OrderStatus.Canceled);
                writeToFileJSON();
                return;
            }
        }
    }

    public boolean changeOrderStatus(String uniqueId, String status, String rejectionReason)
    {
        for(Order order : orders.values())
        {
            if(order.getUniqueId().equals(uniqueId))
            {
                if(status.equals("Approve"))
                {
                    order.setStatus(OrderStatus.Approved);
                } else if (status.equals("Deny")) {
                    order.setStatus(OrderStatus.Rejected);
                    order.setRejectionReason(rejectionReason);
                }else if (status.equals("Taken")) {
                    if(order.getRentalStartDate().getTime() < new Date().getTime())
                    {
                        order.setStatus(OrderStatus.Taken);
                    }else {
                        return false;
                    }
                }else if (status.equals("Returned")) {
                    order.setStatus(OrderStatus.Returned);
                }
                orders.put(order.getUniqueId(), order);
                writeToFileJSON();
                return true;
            }
        }
        return false;
    }

    private void writeToFileJSON()
    {
        try
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file),orders);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void readFromFileJSON()
    {
        try
        {
            JavaType type = objectMapper.getTypeFactory().constructMapType(HashMap.class,String.class, Order.class);
            orders = objectMapper.readValue(file,type);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void createFile()
    {
        try{
            if(!file.exists()) file.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
