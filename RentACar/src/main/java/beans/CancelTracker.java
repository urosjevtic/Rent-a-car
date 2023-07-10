package beans;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class CancelTracker {
    private String id;
    private int canceled;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date firstCancel;
    private String userId;

    public CancelTracker(int canceled, Date firstCancel, String userId) {
        this.canceled = canceled;
        this.firstCancel = firstCancel;
        this.userId = userId;
    }

    public CancelTracker(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCanceled() {
        return canceled;
    }

    public void setCanceled(int canceled) {
        this.canceled = canceled;
    }

    public Date getFirstCancel() {
        return firstCancel;
    }

    public void setFirstCancel(Date firstCancel) {
        this.firstCancel = firstCancel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
