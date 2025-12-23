package com.unknownclinic.appointment.mapper;

import com.unknownclinic.appointment.domain.Administrator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AdministratorMapper {
    Optional<Administrator> findByAdminLoginId(@Param("adminLoginId") String adminLoginId);
}
