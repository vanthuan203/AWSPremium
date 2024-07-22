package com.nts.awspremium.repositories;

import com.nts.awspremium.model.AccountChange;
import com.nts.awspremium.model.AccountChangeDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountChangeDailyRepository extends JpaRepository<AccountChangeDaily,Long> {
    @Query(value = "SELECT * FROM AccPremium.accountchange where running=0 and geo='vn' order by priority desc,id asc limit 1",nativeQuery = true)
    public List<AccountChange> getGeoChangerVN( );

    @Query(value = "SELECT * FROM AccPremium.accountchange where running=0 and geo='kr' order by priority desc,id asc limit 1",nativeQuery = true)
    public List<AccountChange> getGeoChangerKR( );

    @Query(value = "SELECT count(*) FROM AccPremium.account_change_daily where id=1 and time=?1",nativeQuery = true)
    public Integer checkRunningChanger(Integer time);
    @Query(value = "SELECT * FROM AccPremium.accountchange where running=0 and geo='us' order by priority desc,id asc limit 1",nativeQuery = true)
    public List<AccountChange> getGeoChangerUS( );
}
