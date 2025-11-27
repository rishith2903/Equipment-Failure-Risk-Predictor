package com.equipmentpredictor.dto;

import lombok.Data;

/**
 * Login Request DTO
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
