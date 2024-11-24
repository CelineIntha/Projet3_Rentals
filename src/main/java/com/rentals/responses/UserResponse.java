package com.rentals.responses;

public record UserResponse(Integer id, String name, String email, String created_at, String updated_at) {
}
