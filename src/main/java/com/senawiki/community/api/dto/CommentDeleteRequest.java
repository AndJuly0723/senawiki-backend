package com.senawiki.community.api.dto;

import jakarta.validation.constraints.Size;

public class CommentDeleteRequest {

    @Size(max = 100)
    private String guestName;

    @Size(max = 100)
    private String guestPassword;

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPassword() {
        return guestPassword;
    }

    public void setGuestPassword(String guestPassword) {
        this.guestPassword = guestPassword;
    }
}
