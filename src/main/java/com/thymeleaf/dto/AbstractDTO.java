package com.thymeleaf.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class AbstractDTO<T> {
    private Integer id;
    private Integer activeFlag;
    private java.sql.Timestamp createdDate;
    private java.sql.Timestamp modifiedDate;
    private List<T> listResult = new ArrayList<>();
}
