package com.nts.awspremium.repositories;

import com.nts.awspremium.model.Account;
import com.nts.awspremium.model.VpsRunning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Entity;
import javax.transaction.Transactional;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    @Query(value = "Select id from account where username=?1 limit 1",nativeQuery = true)
    public Long findIdUsername(String username);
    @Query(value = "Select * from account where username=?1 limit 1",nativeQuery = true)
    public Account findAccountByUsername(String username);

    @Query(value = "Select id from account where username=?1 limit 1",nativeQuery = true)
    public Long findIdByUsername(String username);

    @Query(value = "Select count(*) from account where username=?1 limit 1",nativeQuery = true)
    public Integer checkByUsername(String username);

    @Query(value = "Select proxy,proxy2 from account where id=?1 limit 1",nativeQuery = true)
    public String findProxyByIdSub(Long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO account(username,password,recover,live,running,vps,date,geo) VALUES(?1,?2,?3,?4,?5,?6,?7,?8)",nativeQuery = true)
    public void insertAccountView(String username,String password,String recover,Integer live,Integer running,String vps,String date,String geo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET password=?1,recover=?2,live=?3 where id=?6",nativeQuery = true)
    public void updateAccountView(String password,String recover,Integer live,Long id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET proxy=?1 where id=?2",nativeQuery = true)
    public void updateProxyAccount(String proxy,Long id);

    @Query(value = "Select * from account where id=?1 limit 1",nativeQuery = true)
    public List<Account> findAccountById(Long id);


    @Query(value = "Select * from account where id in(?1)",nativeQuery = true)
    public List<Account> findAccountByListId(List<String> list_orderid);


    @Query(value = "Select * from account where id=?1 limit 1",nativeQuery = true)
    public Account getAccountById(Long id);

    @Query(value = "Select id from account order by rand() limit ?1",nativeQuery = true)
    public List<Long> getAccountRandByLimit(Integer limit);

    @Query(value = "Select id from account where proxy='' order by rand() limit ?1",nativeQuery = true)
    public List<Long> getAccountByLimit(Integer limit);

    @Query(value = "Select count(*) from account where id=?1 and running=0",nativeQuery = true)
    public Integer checkAccountById(Long id);
    @Query(value = "Select password,recover,oldpassword from account where id=?1 limit 1",nativeQuery = true)
    public String getInfo(Long id);


    @Query(value = "Select count(*) from account where id=?1 and vps like ?2 limit 1",nativeQuery = true)
    public Integer checkIdByVps(Long id,String vps);


    @Query(value = "SELECT id  FROM account where (vps is null or vps='' or vps=' ') and running=0 and live=1 and round((endtrial/1000-UNIX_TIMESTAMP())/60/60/24) >=1 order by rand()  limit 1",nativeQuery = true)
    public Long getAccount();

    @Query(value = "call reset_account()",nativeQuery = true)
    public Integer call_Reset_Account();

    @Query(value = "call changer_account(?1,?2,?3)",nativeQuery = true)
    public Integer changer_account(String geo,String mail,Integer limit);

    @Query(value = "SELECT count(*) FROM account where geo=?1 ",nativeQuery = true)
    public Integer checkCountAccChanger(String geo);

    @Query(value = "SELECT id  FROM account where (vps is null or vps='' or vps=' ') and running=0 and live=1 and geo=?1 order by rand()  limit 1",nativeQuery = true)
    public Long getAccountView(String geo);

    @Query(value = "SELECT id  FROM account where  running=0 and live=1 and geo=?1 and google_suite not in (select id from google_suite where state=0) order by rand()  limit 1",nativeQuery = true)
    public Long getAccountViewByGoogleSuite(String geo);

    @Query(value = "SELECT id  FROM account where live=1 and running=0 and round((endtrial/1000-UNIX_TIMESTAMP())/60/60/24) >=1  order by rand()  limit 1",nativeQuery = true)
    public Long getAccountNeedLogin();

    @Query(value = "SELECT id  FROM account where live=1 and reg=0 and geo='reg' and status=0 and google_suite=?1  order by group_mail asc, rand()  limit 1",nativeQuery = true)
    public Long getAccountREG(String google_suite);

    @Query(value = "SELECT id  FROM account where live=1 and reg=0 and geo='reg' and status=0 and google_suite=?1 and cmt>0 order by group_mail asc, rand()  limit 1",nativeQuery = true)
    public Long getAccountCmtREG(String google_suite);

    @Query(value = "SELECT id FROM account where vps=?1 and running=0 and live=1 and geo=?2 order by rand() limit 1",nativeQuery = true)
    public Long getaccountByVps(String vps,String geo);

    @Query(value = "SELECT id FROM account where running=0 and live=1 and geo='tiktok' order by rand() limit 1",nativeQuery = true)
    public Long get_acc_tiktok();


    @Query(value = "SELECT proxy FROM account where username=?1 limit 1",nativeQuery = true)
    public String getProxyByUsername(String username);


    @Query(value = "SELECT count(*) FROM account where live=1",nativeQuery = true)
    public Integer getCountGmailLiveView();

    @Query(value = "SELECT count(*) FROM account",nativeQuery = true)
    public Integer getCountGmailBuffh();

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0,vps='',proxy='' where vps=?1",nativeQuery = true)
    public Integer resetAccountByVps(String vps);


    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET reg=0,get_time=0 where geo='reg' and reg=1 and status=0 and round((UNIX_TIMESTAMP()-get_time/1000)/60)>10",nativeQuery = true)
    public Integer resetAccountRegByThan10m();

    @Modifying
    @Transactional
    @Query(value = "UPDATE account set running=2 where  running!=2 and round((UNIX_TIMESTAMP()-start_time/1000)/60/60)>=?1 and start_time!=0",nativeQuery = true)
    public Integer resetAccountViewByThanDay(Integer hour);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0,vps='',proxy='' where geo not like 'cmt%' and vps=?1",nativeQuery = true)
    public Integer resetAccountViewByVps(String vps);


    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0,vps='',proxy='' where geo like 'cmt%' and vps=?1",nativeQuery = true)
    public Integer resetAccountCmtByVps(String vps);
    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0,vps='',live=?1,proxy='' where id=?2",nativeQuery = true)
    public Integer resetAccountByUsername(Integer live,Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0,vps='',live=?1,proxy='',geo=?2 where id=?3",nativeQuery = true)
    public Integer resetAccountGeoByUsername(Integer live,String geo,Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=2,vps='',proxy='',geo=?1,end_time=?2 where username=?2",nativeQuery = true)
    public Integer resetAccountByUsernameThanDay(String geo,Long end_time,String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0,vps='',live=?1,proxy='',start_time=0,geo=?2 where id=?3",nativeQuery = true)
    public Integer resetAccountGeoStartTimeByUsername(Integer live,String geo,Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET proxy=?1,proxy2=?2 where id=?3",nativeQuery = true)
    public Integer updateProxyById(String proxy,String proxy2,Long id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0 where vps=?1",nativeQuery = true)
    public void updateRunningByVPs(String vps);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET running=0 where vps=?1 and geo like 'cmt%'",nativeQuery = true)
    public void updateRunningAccCmtByVPs(String vps);
    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET timecheck=?1,running=1 where id=?2",nativeQuery = true)
    public Integer updateTimecheckById(Long timecheck,Long id);



    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET vps='',running=0 where vps=?1 and INSTR(?2,username)=0",nativeQuery = true)
    public Integer updateListAccount(String vps,String listacc);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET vps='',running=0 where round((UNIX_TIMESTAMP()-timecheck/1000)/60/60)>=24 and live=1 and round((endtrial/1000-UNIX_TIMESTAMP())/60/60/24) >=1 and running=1",nativeQuery = true)
    public Integer resetAccountByTimecheck();

    @Query(value = "SELECT vps,round(0) as time,count(*) as total FROM account group by vps order by total desc",nativeQuery = true)
    public List<VpsRunning> getCountAccByVps();

    @Query(value = "SELECT geo FROM account where username=?1 limit 1",nativeQuery = true)
    public String getGeoByUsername(String username);

}
