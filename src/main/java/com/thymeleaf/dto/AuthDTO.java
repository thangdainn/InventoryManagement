package com.thymeleaf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO extends AbstractDTO<AuthDTO>{
    private Integer roleId;
    private Integer menuId;
    private Integer permission;
}
