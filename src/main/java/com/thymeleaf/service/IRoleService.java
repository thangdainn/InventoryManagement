package com.thymeleaf.service;

import com.thymeleaf.dto.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRoleService {
    RoleDTO findByName(RoleDTO dto);
    List<RoleDTO> findAll();
    List<RoleDTO> findByUser_Id(Integer id);
    RoleDTO save(RoleDTO dto);
    Page<RoleDTO> findAll(Pageable pageable);
    Page<RoleDTO> findByNameContaining(String keyword, Pageable pageable);
    RoleDTO findById(Integer id);
    void delete(Integer[] ids);
}
