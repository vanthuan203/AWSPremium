package com.nts.awspremium.repositories;

import com.nts.awspremium.model.AccountReg24h;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface AccountReg24hRepository extends JpaRepository<AccountReg24h,String> {
    @Modifying
    @Transactional
    @Query(value = "delete from account_reg_24h where round((UNIX_TIMESTAMP()-update_time/1000)/60/60)>24;",nativeQuery = true)
    public Integer deleteAllByThan24h();
    @Query(value = "select count(*) from account_reg_24h where id like ?1",nativeQuery = true)
    public Integer count_Reg_24h_By_GoogleSuite(String google_suite);
}
