package beans;

import beans.enums.CommentStatus;

public class Comment {

    private String id;
    private String customerId;
    private String companyId;
    private String comment;
    private double reviewScore;

    private CommentStatus commentStatus;

    public Comment(String customerId, String companyId, String comment, double reviewScore, CommentStatus commentStatus) {
        this.customerId = customerId;
        this.companyId = companyId;
        this.comment = comment;
        this.reviewScore = reviewScore;
        this.commentStatus = commentStatus;
    }

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(double reviewScore) {
        this.reviewScore = reviewScore;
    }

    public boolean areValuesValid(){
        return (comment != null && !comment.isBlank());
    }

}
