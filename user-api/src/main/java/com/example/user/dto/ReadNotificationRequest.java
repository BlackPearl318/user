package com.example.user.dto;

import org.springframework.stereotype.Component;

@Component
public class ReadNotificationRequest {

    private Long id;

    public ReadNotificationRequest() {
    }

    public ReadNotificationRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

