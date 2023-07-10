package dao;

import beans.Comment;
import beans.Vehicle;
import beans.enums.CommentStatus;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.UpdateCommentDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDAO {

    private HashMap<String, Comment> comments;

    private final ObjectMapper objectMapper;
    private final File file;

    public CommentDAO(String CtxPath)
    {
        objectMapper = new ObjectMapper();
        comments = new HashMap<String, Comment>();

        String filePath = CtxPath + "..\\..\\src\\main\\resources\\Comments.JSON";
        file = new File(filePath);

        createFile();
        readFromFileJSON();

    }

    public boolean save(Comment comment)
    {
        if(!comment.areValuesValid()) return false;

        comment.setId(nextId().toString());
        comments.put(comment.getId(),comment);

        writeToFileJSON();
        readFromFileJSON();

        return true;

    }

    public double getRatingForObject(String objectId){
        List<Comment> acceptedComments = comments.values().stream()
                                         .filter(x-> x.getCompanyId().equals(objectId) && x.getCommentStatus().equals(CommentStatus.Accepted))
                                         .collect(Collectors.toList());

        if(acceptedComments.isEmpty()) return 0.0;

        double ratingSum=0.0;
        for(Comment c:acceptedComments)
        {
            ratingSum+=c.getReviewScore();
        }

        return ratingSum/acceptedComments.size();
    }

    public Collection<Comment> getPendingComments(String objectId)
    {
        return comments.values().stream()
                .filter(x -> x.getCompanyId().equals(objectId) && x.getCommentStatus() == CommentStatus.Pending)
                .collect(Collectors.toList());

    }

    public Collection<Comment> getAcceptedComments(String objectId)
    {
        return comments.values().stream()
                .filter(x -> x.getCompanyId().equals(objectId) && x.getCommentStatus() == CommentStatus.Accepted)
                .collect(Collectors.toList());

    }

    public Collection<Comment> getDeniedComments(String objectId)
    {
        return comments.values().stream()
                .filter(x -> x.getCompanyId().equals(objectId) && x.getCommentStatus() == CommentStatus.Denied)
                .collect(Collectors.toList());

    }

    public boolean updateCommentStatus(UpdateCommentDTO updateComment){
        Comment comment = comments.values().stream().
                filter(x-> x.getId().equals(updateComment.commentId) && x.getCommentStatus() == CommentStatus.Pending).
                findFirst().orElse(null);

        if(comment == null) return false;

        comment.setCommentStatus(updateComment.newCommentStatus);
        writeToFileJSON();
        readFromFileJSON();
        return true;

    }

    public boolean commentExistsForUser(String userId,String objectId){
        for(Comment c: comments.values())
        {
            if(c.getCustomerId().equals(userId) && c.getCompanyId().equals(objectId)) return true;
        }
        return false;
    }

    public Collection<Comment> getByObjectId(String id)
    {
        Collection<Comment> commentsById = new ArrayList<Comment>();

        for(Comment c:comments.values())
        {
            if(c.getCompanyId().equals(id)) commentsById.add(c);
        }

        return commentsById;

    }
    private Integer nextId(){ return comments.keySet().size()+1;}

    private void writeToFileJSON()
    {
        try
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileOutputStream(file),comments);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    private void readFromFileJSON()
    {
        try
        {
            JavaType type = objectMapper.getTypeFactory().constructMapType(HashMap.class,String.class, Comment.class);
            comments = objectMapper.readValue(file,type);
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
