package org.example;

import lombok.Data;

@Data
public class LoginResponsePojo {
    private String token;
    private String userId;
    private String message;
}
