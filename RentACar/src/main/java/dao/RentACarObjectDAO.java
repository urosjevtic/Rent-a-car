package dao;

import beans.Location;
import beans.RentACarObject;
import beans.WorkingHours;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CitiesDTO;
import dto.ObjectDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class RentACarObjectDAO {

    private HashMap<String, RentACarObject> rentObjects;
    private final ObjectMapper objectMapper;
    private final File file;

    public RentACarObjectDAO(String CtxPath)
    {
        objectMapper = new ObjectMapper();
        rentObjects = new HashMap<String,RentACarObject>();

        String filePath = CtxPath + "..\\..\\src\\main\\resources\\RentACarObjectDao.JSON";
        file = new File(filePath);

        createFile();
        //initTestValues();
        readFromFileJSON();

    }

    public Collection<RentACarObject> getAll()
    {
        updateIsOpen();
        return sortByIsOpen(rentObjects.values());
    }

    public RentACarObject getById(String id)
    {
        updateIsOpen();
        return rentObjects.get(id);
    }

    public Collection<CitiesDTO> getAllCities()
    {
        HashMap<String,CitiesDTO> cities = new HashMap<String,CitiesDTO>();

        for(RentACarObject object: rentObjects.values()){
            if(!cities.containsKey(object.getLocation().getCity()))
            {
                CitiesDTO city = new CitiesDTO();
                city.city = object.getLocation().getCity();
                cities.put(city.city,city);
            }
        }

        return cities.values();
    }


    public ObjectDTO save(RentACarObject rentObject)
    {
        if(rentObjects.containsKey(rentObject.getId()) || !rentObject.areValuesValid()) return null;


        rentObject.setId(nextId().toString());
        rentObjects.put(rentObject.getId(),rentObject);
        writeToFileJSON();
        readFromFileJSON();

        ObjectDTO objectDTO = new ObjectDTO();
        objectDTO.id = rentObject.getId();

        return objectDTO;
    }
    private Integer nextId() {return rentObjects.keySet().size()+1;}

    private Collection<RentACarObject> sortByIsOpen(Collection<RentACarObject> objects)
    {
        List<RentACarObject> objectsSorted = new ArrayList<>(objects);
        Collections.sort(objectsSorted, new Comparator<RentACarObject>() {
            @Override
            public int compare(RentACarObject abc1, RentACarObject abc2) {
                return Boolean.compare(abc2.isOpen(),abc1.isOpen());
            }
        });

        return objectsSorted;
    }

    public boolean updateRating(String objectId,double newRating){
        if(newRating < 0 || newRating > 5) return false;

        RentACarObject object = rentObjects.values().stream()
                                .filter(x-> x.getId().equals(objectId))
                                .findAny().orElse(null);

        if(object == null) return false;

        object.setRating(newRating);
        writeToFileJSON();
        readFromFileJSON();
        return true;

    }
    private void updateIsOpen()
    {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String currentTimeString = format.format(currentTime);
        for(RentACarObject rent: rentObjects.values())
        {
            WorkingHours wrhHr = rent.getWorkingHours();
            try {
                LocalTime current = LocalTime.parse(currentTimeString);
                LocalTime from = LocalTime.parse(wrhHr.getFrom());
                LocalTime to = LocalTime.parse(wrhHr.getTo());

                rent.setOpen(current.isAfter(from) && current.isBefore(to));
            }catch(DateTimeParseException e) { e.printStackTrace(); }
        }

    }
    private void writeToFileJSON()
    {
        try
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file),rentObjects);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void readFromFileJSON()
    {
        try
        {
            JavaType type = objectMapper.getTypeFactory().constructMapType(HashMap.class,String.class, RentACarObject.class);
            rentObjects = objectMapper.readValue(file,type);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void createFile()
    {
        try {
            if (!file.exists()) file.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void initTestValues()
    {

        Location location1 = new Location(42,42,"Albeququequeq","42","Albequreure","42");
        RentACarObject object1 = new RentACarObject("Los Pollos Hermanos",true,location1,"gus.jpeg",5,new WorkingHours("9:00","17:00"));

        object1.setId(nextId().toString());

        rentObjects.put(object1.getId(),object1);

        writeToFileJSON();
    }

}
