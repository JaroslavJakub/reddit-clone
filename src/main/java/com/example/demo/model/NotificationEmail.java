package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmail {

    private String subject;

    private String recipient;

    //private String body;

    private Map<String, Object> variables;

    private String template;


}
