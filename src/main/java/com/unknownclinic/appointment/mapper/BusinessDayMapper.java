package com.unknownclinic.appointment.mapper;

import com.unknownclinic.appointment.domain.BusinessDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BusinessDayMapper {
    List<BusinessDay> findAvailableBusinessDays();
    List<BusinessDay> findAllBusinessDays();
    Optional<BusinessDay> findByBusinessDate(@Param("businessDate") LocalDate businessDate);
    void insert(BusinessDay businessDay);
    void update(BusinessDay businessDay);
    void delete(@Param("businessDayId") Long businessDayId);
    void updateAvailability(@Param("businessDate") LocalDate businessDate, @Param("isAvailable") Boolean isAvailable);
}
