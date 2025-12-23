package com.unknownclinic.appointment.service;

import com.unknownclinic.appointment.domain.BusinessDay;
import com.unknownclinic.appointment.mapper.BusinessDayMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BusinessDayService {
    
    private final BusinessDayMapper businessDayMapper;

    public BusinessDayService(BusinessDayMapper businessDayMapper) {
        this.businessDayMapper = businessDayMapper;
    }

    /**
     * 利用可能な営業日一覧を取得
     */
    @Transactional(readOnly = true)
    public List<BusinessDay> getAvailableBusinessDays() {
        return businessDayMapper.findAvailableBusinessDays();
    }

    /**
     * 全ての営業日一覧を取得
     */
    @Transactional(readOnly = true)
    public List<BusinessDay> getAllBusinessDays() {
        return businessDayMapper.findAllBusinessDays();
    }

    /**
     * 営業日を追加
     */
    public BusinessDay addBusinessDay(LocalDate businessDate) {
        if (businessDayMapper.findByBusinessDate(businessDate).isPresent()) {
            throw new IllegalArgumentException("この日は既に営業日として登録されています");
        }

        BusinessDay businessDay = new BusinessDay();
        businessDay.setBusinessDate(businessDate);
        businessDay.setIsAvailable(true);
        
        businessDayMapper.insert(businessDay);
        return businessDay;
    }

    /**
     * 営業日を削除
     */
    public void deleteBusinessDay(Long businessDayId) {
        businessDayMapper.delete(businessDayId);
    }

    /**
     * 予約受付可否を更新
     */
    public void updateAvailability(LocalDate businessDate, Boolean isAvailable) {
        businessDayMapper.updateAvailability(businessDate, isAvailable);
    }
}
