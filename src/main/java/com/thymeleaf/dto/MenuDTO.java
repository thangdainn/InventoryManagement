package com.thymeleaf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO extends AbstractDTO<MenuDTO>{
    private Integer parentId;
    private String url;
    private String name;
    private Integer orderIndex;
    private String idMenu;
    private List<AuthDTO> auths = new ArrayList<>();
    private List<MenuDTO> child = new ArrayList<>();
    private Map<Integer, Integer> mapAuth;
}
