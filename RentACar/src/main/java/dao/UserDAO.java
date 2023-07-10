package dao;

import beans.RentACarObject;
import beans.User;
import beans.enums.CustomerType;
import beans.enums.Role;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ManagersDTO;
import dto.UpdateManagerObjectDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class UserDAO {

    private List<User> users;
    private final ObjectMapper objectMapper;
    private final File file;

    public UserDAO(String CtxPath){

        objectMapper = new ObjectMapper();
        users = new ArrayList<User>();

        String filePath = CtxPath + "..\\..\\src\\main\\resources\\Users.JSON";
        file = new File(filePath);

        createFile();
        readFromFileJSON();
    }

    public User findUser(User user){
        for(User u: users)
        {
            if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) return u;
        }
        return null;
    }

    public User getByUsername(String username){
        readFromFileJSON();
        for(User u: users)
        {
            if(u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public User getById(String id){
        readFromFileJSON();
        for(User u: users)
        {
            if(u.getUserId().equals(id)) return u;
        }
        return null;
    }
    public List<User> getAll(){
        readFromFileJSON();
        return users;
    }

    public Collection<ManagersDTO> getAllFreeManagers()
    {
        Collection<ManagersDTO> managers = new ArrayList<ManagersDTO>();

        for(User u:users)
        {
            if(u.getRole() == Role.Manager && u.getRentACarObjectId() == null)
            {
                ManagersDTO manager = new ManagersDTO();
                manager.id = u.getUserId();
                manager.fullNameUsername = u.getName() + " " + u.getLastName() + " : " + u.getUsername();
                managers.add(manager);
            }
        }

        return managers;
    }

    public User save(User user){
        if(!isUsernameUnique(user)) return null;

        user.setUserId(nextId().toString());
        user.setIsBlocked(false);
        user.setCustomerType(CustomerType.None);
        users.add(user);
        writeToFileJSON();
        readFromFileJSON();
        return user;
    }

    public UpdateManagerObjectDTO updateObjectInManager(UpdateManagerObjectDTO upd)
    {
        if(isObjectAssigned(upd.objectId)) return null;

        for(User u:users)
        {
            if(u.getUserId().equals(upd.managerId) && u.getRole() == Role.Manager)
            {
                u.setRentACarObjectId(upd.objectId);
                writeToFileJSON();
                readFromFileJSON();
                return upd;

            }
        }

        return null;

    }

    private boolean isObjectAssigned(String id)
    {
        for(User u: users)
        {
            if(u.getRentACarObjectId() != null && u.getRentACarObjectId().equals(id)) return true;
        }
        return false;
    }

    public User update(User user)
    {
        if(!validateValues(user)) return null;
        for(User u:users)
        {
            if(user.getUserId().equals(u.getUserId()))
            {
                u.setUsername(user.getUsername());
                u.setName(user.getName());
                u.setLastName(user.getLastName());
                u.setBirthDate(user.getBirthDate());
                u.setGender(user.getGender());

                writeToFileJSON();
                readFromFileJSON();
                return user;
            }
        }

        return null;

    }



    public Boolean validateValues(User user)
    {
        return !user.getUsername().isBlank() &&
                !user.getName().isBlank() &&
                !user.getLastName().isBlank()
                ;
    }

    private boolean isUsernameUnique(User user){
        for(User u:users)
        {
            if(u.getUsername().equals(user.getUsername())) return false;
        }
        return true;
    }
    private Integer nextId(){
        return users.size()+1;
    }

    public void modifyPoints(String userId, int points)
    {
        for(User user : users)
        {
            if(user.getUserId().equals(userId))
            {
                user.setPoints(user.getPoints() + points);
                changeCustomerType(user);
                update(user);
                return;
            }
        }
    }

    private void changeCustomerType(User user)
    {
        if(user.getPoints() >= 3000)
        {
            user.setCustomerType(CustomerType.Gold);
        } else if (user.getPoints() >= 2000) {
            user.setCustomerType(CustomerType.Silver);
        } else if (user.getPoints() >= 1000) {
            user.setCustomerType(CustomerType.Bronze);
        }else {
            user.setCustomerType(CustomerType.None);
        }
    }

    private void writeToFileJSON()
    {

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file), users);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void readFromFileJSON()
    {
        try{
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class,User.class);
            users = objectMapper.readValue(file, type);
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
