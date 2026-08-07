package org.example;

import lombok.Data;

import java.util.List;

@Data
public class AddPlace {
    private Location location;
    private int accuracy;
    private String name;
    private String phone_number;
    private String address;
    private List<String> types;
    private String website;
    private String language;

}
