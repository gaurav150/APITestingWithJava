package org.example;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderPojo {

    private List<CreateOrderDetailsInPojo> orders;
}
