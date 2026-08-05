package org.example;

import lombok.Data;

import java.util.List;

@Data
public class Courses {

    private List<Course> webAutomation;
    private List<Course> api;
    private List<Course> mobile;
}
