package dao;

import beans.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CartOrderDTO;
import dto.RentDateDTO;
import dto.RentVehicleDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopingCartDAO {

    private HashMap<String, ShopingCart> shopingCarts;

    private final ObjectMapper objectMapper;
    private final File file;

    public ShopingCartDAO(String CtxPath)
    {
        objectMapper = new ObjectMapper();
        shopingCarts = new HashMap<String, ShopingCart>();

        String filePath = CtxPath + "..\\..\\src\\main\\resources\\ShopingCarts.JSON";
        file = new File(filePath);

        createFile();
        readFromFileJSON();

    }

    private Integer nextId(){ return shopingCarts.keySet().size()+1;}

    public ShopingCart save(ShopingCart shopingCart)
    {
        shopingCart.setId(nextId().toString());
        shopingCarts.put(shopingCart.getId(),shopingCart);

        writeToFileJSON();
        readFromFileJSON();

        return shopingCart;

    }

    public ShopingCart getByUserId(String userId)
    {
        readFromFileJSON();
        for(ShopingCart shopingCart : shopingCarts.values())
        {
            if(shopingCart.getUserId().equals(userId))
            {
                return shopingCart;
            }
        }
        return null;
    }

    public void update(ShopingCart newShopingCart)
    {
        for(ShopingCart shopingCart : shopingCarts.values())
        {
            if(shopingCart.getId().equals(newShopingCart.getId()))
            {
                shopingCart.setVehicleIds(newShopingCart.getVehicleIds());
                writeToFileJSON();
                readFromFileJSON();
                return;
            }
        }
        createShopingCart(newShopingCart);
    }
    private void createShopingCart(ShopingCart newShopingCart)
    {
        shopingCarts.put(nextId().toString(), newShopingCart);
        writeToFileJSON();
        readFromFileJSON();
    }

    public double getCurrenPriceForUser(String userId)
    {
        for(ShopingCart shopingCart : shopingCarts.values())
        {
            if(shopingCart.getUserId().equals(userId))
            {
                return shopingCart.getPrice();
            }
        }
        return 0;
    }

    public RentDateDTO getCurrenOrderDateForUser(String userId)
    {
        for(ShopingCart shopingCart : shopingCarts.values())
        {
            if(shopingCart.getUserId().equals(userId))
            {
                RentDateDTO dateDTO = new RentDateDTO();
                dateDTO.rentStartDate = shopingCart.getRentDateStart();
                dateDTO.rentEndDate = shopingCart.getRentDateEnd();
                return dateDTO;
            }
        }
        return null;
    }

    public boolean setCurrenOrderDateForUser(String userId, CartOrderDTO cartOrderDTO)
    {
        if(cartOrderDTO.isValid())
        {
            for(ShopingCart shopingCart : shopingCarts.values())
            {
                if(shopingCart.getUserId().equals(userId))
                {
                    shopingCart.setRentDateStart(cartOrderDTO.rentStartDate);
                    shopingCart.setRentDateEnd(cartOrderDTO.rentEndDate);
                    shopingCart.setObjectId(cartOrderDTO.objectId);
                    update(shopingCart);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeItem(String userId, RentVehicleDTO vehical)
    {
        for(ShopingCart shopingCart : shopingCarts.values())
        {
            if(shopingCart.getUserId().equals(userId))
            {
                 shopingCart.removeVehicle(vehical.id);
                 shopingCart.setPrice(shopingCart.getPrice() - vehical.price);
                 update(shopingCart);
                return true;
            }
        }
        return false;
    }

    public void empty(String userId)
    {
        for(ShopingCart shopingCart : shopingCarts.values())
        {
            if(shopingCart.getUserId().equals(userId))
            {
                shopingCart.setPrice(0);
                List<String> emptyList = new ArrayList<>();
                shopingCart.setVehicleIds(emptyList);
                shopingCart.setRentDateStart(null);
                shopingCart.setRentDateEnd(null);
                shopingCart.setObjectId(null);

                update(shopingCart);
            }
        }
    }
    private void writeToFileJSON()
    {
        try
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file),shopingCarts);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private void readFromFileJSON()
    {
        try
        {
            JavaType type = objectMapper.getTypeFactory().constructMapType(HashMap.class,String.class, ShopingCart.class);
            shopingCarts = objectMapper.readValue(file,type);
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
