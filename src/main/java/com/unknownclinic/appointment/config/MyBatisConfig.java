package com.unknownclinic.appointment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.unknownclinic.appointment.mapper")
public class MyBatisConfig {
    // MyBatis設定（application.ymlで設定済み）
}
