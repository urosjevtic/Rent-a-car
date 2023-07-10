package dto;

import beans.enums.CommentStatus;

public class UpdateCommentDTO {
    public String commentId;
    public String objectId;
    public CommentStatus newCommentStatus;
}
