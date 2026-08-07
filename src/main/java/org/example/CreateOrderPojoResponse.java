package org.example;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderPojoResponse {
    private List<String> orders;
    private List<String> productOrderId;
    private String message;
}
