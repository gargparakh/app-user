package com.example.parakh.user_grievance;

import java.util.Date;

/**
 * Created by Parakh Garg on 16-04-2018.
 */


public class ChatMessage {
    private Integer id;
    private String role;
    private String chats;
    private Date createdAt;
    private Date updatedAt;
    private Integer complaintsId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getChats() {
        return chats;
    }

    public void setChats(String chats) {
        this.chats = chats;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getComplaintsId() {
        return complaintsId;
    }

    public void setComplaintsId(Integer complaintsId) {
        this.complaintsId = complaintsId;
    }
}
