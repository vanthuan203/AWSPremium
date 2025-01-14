package com.nts.awspremium.repositories;

import com.nts.awspremium.model.AccountTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface AccountTaskRepository extends JpaRepository<AccountTask,Long> {

    @Query(value = "Select * from account_task  where account_id=?1 limit 1",nativeQuery = true)
    public AccountTask get_Acount_Task_By_AccountId(String  account_id);

    @Modifying
    @Transactional
    @Query(value = "update account_task set task_success_24h=?1 where account_id=?2",nativeQuery = true)
    public Integer update_Total_Success_24h(Integer task_success_24h,String  account_id);

}
