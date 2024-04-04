package com.thymeleaf.api.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthInput {
    private String role;
    private String menu;
    private Integer activeFlag = 1;
}
