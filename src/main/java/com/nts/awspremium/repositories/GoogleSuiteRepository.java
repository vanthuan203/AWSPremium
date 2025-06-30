package com.nts.awspremium.repositories;

import com.nts.awspremium.model.DataOrder;
import com.nts.awspremium.model.GoogleSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoogleSuiteRepository extends JpaRepository<GoogleSuite,Long> {

    @Query(value = "select * from google_suite where id=?1 limit 1",nativeQuery = true)
    public GoogleSuite get_Google_Suite(String google_suite);

    @Query(value = "SELECT GROUP_CONCAT(id) FROM AccPremium.google_suite;",nativeQuery = true)
    public String get_List_Google_Suite();

}
