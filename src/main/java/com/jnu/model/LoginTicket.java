package com.jnu.model;

import java.util.Date;

/**
 * Created by Damon on 2017/8/14.
 */
public class LoginTicket {
    private int id;
    private int userId;
    private Date expired;//过期时间
    private int status;
    private String ticket;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getExpird() {
        return expired;
    }

    public void setExpird(Date expird) {
        this.expired = expird;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
