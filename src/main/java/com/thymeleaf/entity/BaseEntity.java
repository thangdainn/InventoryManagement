package com.thymeleaf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "active_flag")
    private Integer activeFlag;

    @PrePersist
    public void prePersist() {
        if (activeFlag == null) {
            activeFlag = 1;
        }
    }

    @Column(name = "created_date")
    @CreatedDate
    private Timestamp createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private Timestamp modifiedDate;
}
