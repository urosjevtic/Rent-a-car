package dao;

import beans.CancelTracker;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CancelTrackerDAO {

    private HashMap<String, CancelTracker> trackers;
    private final ObjectMapper objectMapper;
    private final File file;


    public CancelTrackerDAO(String CtxPath)
    {
        objectMapper = new ObjectMapper();
        trackers = new HashMap<String,CancelTracker>();

        String filePath = CtxPath + "..\\..\\src\\main\\resources\\CancelTracker.JSON";
        file = new File(filePath);

        createFile();
        readFromFileJSON();

    }

    public CancelTracker save(CancelTracker tracker)
    {
        tracker.setId(nextId().toString());
        trackers.put(tracker.getId(),tracker);

        writeToFileJSON();
        readFromFileJSON();

        return tracker;
    }

    public CancelTracker getByUserId(String userId)
    {
        return trackers.values().stream()
                .filter(x -> x.getUserId().equals(userId))
                .findFirst().orElse(null);
    }

    public boolean updateTracker(CancelTracker updateTracker)
    {
        CancelTracker tracker = trackers.values().stream()
                .filter(x -> x.getId().equals(updateTracker.getId()))
                .findFirst().orElse(null);
        if(tracker == null) return false;

        tracker.setCanceled(updateTracker.getCanceled());
        tracker.setFirstCancel(updateTracker.getFirstCancel());

        writeToFileJSON();
        readFromFileJSON();

        return true;
    }


    private Integer nextId(){ return trackers.keySet().size()+1;}

    private void writeToFileJSON()
    {
        try
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file),trackers);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private void readFromFileJSON()
    {
        try
        {

                JavaType type = objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, CancelTracker.class);
                trackers = objectMapper.readValue(file, type);


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
