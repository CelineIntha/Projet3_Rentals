package com.rentals.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private Integer id;
    private String message;
    private String created_at;
    private String updated_at;
    private Integer user_id;
    private Integer rental_id;

    public MessageResponse(Integer id, String message, String created_at, String updated_at, Integer user_id, Integer rental_id) {
        this.id = id;
        this.message = message;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.user_id = user_id;
        this.rental_id = rental_id;
    }
}
