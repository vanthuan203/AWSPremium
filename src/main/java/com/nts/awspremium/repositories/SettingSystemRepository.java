package com.nts.awspremium.repositories;

import com.nts.awspremium.model.SettingSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingSystemRepository extends JpaRepository<SettingSystem,Long> {



    @Query(value = "select count(*) from INFORMATION_SCHEMA.PROCESSLIST where db = 'Data' and COMMAND='Query' and TIME>0",nativeQuery = true)
    public Integer check_MySQL();

    @Query(value = "select * from setting_system where id=1",nativeQuery = true)
    public SettingSystem get_Setting_System();

}
