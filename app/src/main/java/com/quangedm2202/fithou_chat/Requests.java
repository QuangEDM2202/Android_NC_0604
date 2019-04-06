package com.quangedm2202.fithou_chat;

/**
 * Created by Admin on 25/03/2019.
 */

public class Requests {
    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String request_type;
    public Requests() {
    }

    public Requests(String request_type) {
        this.request_type = request_type;
    }
}
