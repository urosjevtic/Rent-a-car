package dao;

import beans.Vehicle;
import beans.enums.FuelType;
import beans.enums.VehicleType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ObjectDTO;
import dto.RentVehicleDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class VehicleDAO {

    private HashMap<String, Vehicle> vehicles;
    private final ObjectMapper objectMapper;
    private final File file;

    public VehicleDAO(String CtxPath)
    {
        objectMapper = new ObjectMapper();
        vehicles = new HashMap<String, Vehicle>();

        String filePath = CtxPath + "..\\..\\src\\main\\resources\\Vehicles.JSON";
        file = new File(filePath);

        createFile();
        readFromFileJSON();

    }

    public Vehicle save(Vehicle vehicle)
    {

        if(!vehicle.areValuesValid()) return null;

        vehicle.setId(nextId().toString());
        vehicles.put(vehicle.getId(),vehicle);

        writeToFileJSON();
        readFromFileJSON();

        return vehicle;

    }

    public Collection<ObjectDTO> getObjectsWithVehicleType(VehicleType vehicleType)
    {
        HashMap<String,ObjectDTO> objects = new HashMap<String,ObjectDTO>();

        for(Vehicle v:vehicles.values())
        {
            if(!objects.containsKey(v.getCompanyId()) && v.getVehicleType() == vehicleType)
            {
                ObjectDTO object = new ObjectDTO();
                object.id = v.getCompanyId();
                objects.put(object.id,object);
            }
        }

        return objects.values();
    }

    public Collection<ObjectDTO> getObjectsWithFuelType(FuelType fuelType)
    {
        HashMap<String,ObjectDTO> objects = new HashMap<String,ObjectDTO>();

        for(Vehicle v:vehicles.values())
        {
            if(!objects.containsKey(v.getCompanyId()) && v.getFuelType() == fuelType)
            {
                ObjectDTO object = new ObjectDTO();
                object.id = v.getCompanyId();
                objects.put(object.id,object);
            }
        }

        return objects.values();
    }

    public Collection<Vehicle> getAll()
    {
        Collection<Vehicle> allVehicles = new ArrayList<>();
        allVehicles.addAll(vehicles.values());
        return allVehicles;
    }

    public Vehicle getById(String id)
    {
        Collection<Vehicle> vehiclesById =  new ArrayList<Vehicle>();

        for(Vehicle v : vehicles.values())
        {
            if(v.getId().equals(id)) return v;
        }

        return null;
    }

    public Collection<Vehicle> getByObjectId(String id)
    {
        Collection<Vehicle> vehiclesById =  new ArrayList<Vehicle>();

        for(Vehicle v : vehicles.values())
        {
            if(v.getCompanyId().equals(id)) vehiclesById.add(v);
        }

        return vehiclesById;
    }

    public boolean updateVehicle(Vehicle vehicle)
    {
        if(!vehicle.areValuesValid()) return false;

        vehicles.put(vehicle.getId(),vehicle);
        writeToFileJSON();
        readFromFileJSON();
        return true;
    }

    public boolean deleteVehicle(Vehicle vehicle)
    {
        String keyMatch = "";
        for(String key: vehicles.keySet()){
            if(vehicle.getId().equals(key))
            {
                keyMatch = key;
                break;
            }
        }

        if(keyMatch.equals("")) return false;

        Vehicle tmp = vehicles.remove(keyMatch);
        vehicles.put("DELETED_"+keyMatch,tmp);

        writeToFileJSON();
        readFromFileJSON();

        return true;
    }


    private Integer nextId() { return vehicles.keySet().size()+1;}

    public Collection<Vehicle> getAllAvailable()
    {
        Collection<Vehicle> availableVehicles = new ArrayList<Vehicle>();
        for(Vehicle v : vehicles.values())
        {
            if(!v.isRented())
            {
                availableVehicles.add(v);
            }
        }
        return availableVehicles;
    }

    public void changeStatus(String vehicleId, boolean isRented)
    {
        Vehicle vehicle = getById(vehicleId);
        vehicle.setRented(isRented);
        vehicles.put(vehicle.getId(), vehicle);
        writeToFileJSON();
    }

    private void writeToFileJSON()
    {
        try
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file),vehicles);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private void readFromFileJSON()
    {
        try
        {
            JavaType type = objectMapper.getTypeFactory().constructMapType(HashMap.class,String.class, Vehicle.class);
            vehicles = objectMapper.readValue(file,type);
            removeDeleted();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void removeDeleted()
    {
        List<String> keysToBeRemoved = new ArrayList<String>();
        for(String key: vehicles.keySet())
        {
            if(key.contains("DELETED")) keysToBeRemoved.add(key);
        }
        for(String key:keysToBeRemoved)
        {
            vehicles.remove(key);
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
