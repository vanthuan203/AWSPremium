package com.nts.awspremium.repositories;

import com.nts.awspremium.model.DataOrder;
import com.nts.awspremium.model.GoogleSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface GoogleSuiteRepository extends JpaRepository<GoogleSuite,Long> {

    @Query(value = "select * from google_suite where id=?1 limit 1",nativeQuery = true)
    public GoogleSuite get_Google_Suite(String google_suite);

    @Modifying
    @Transactional
    @Query(value = "update google_suite set state=1,update_time=0 where  state=0 and round((UNIX_TIMESTAMP()-update_time/1000)/60/60)>12;",nativeQuery = true)
    public Integer update_State_Google_Suite();

    @Query(value = "SELECT GROUP_CONCAT(id) FROM AccPremium.google_suite;",nativeQuery = true)
    public String get_List_Google_Suite();

}
