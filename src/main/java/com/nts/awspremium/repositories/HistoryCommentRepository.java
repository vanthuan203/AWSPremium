package com.nts.awspremium.repositories;

import com.nts.awspremium.model.HistoryComment;
import com.nts.awspremium.model.HistoryView;
import com.nts.awspremium.model.VpsRunning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface HistoryCommentRepository extends JpaRepository<HistoryComment,Long> {
    @Query(value = "SELECT * FROM historycomment where username=?1 order by id desc limit 1",nativeQuery = true)
    public List<HistoryComment> get(String username);

    @Query(value = "SELECT * FROM historycomment where id=?1 limit 1",nativeQuery = true)
    public List<HistoryComment> getHistoriesById(Long id);

    @Query(value = "SELECT listvideo FROM historycomment where id=?1 limit 1",nativeQuery = true)
    public String getListVideoById(Long id);
    @Query(value = "SELECT id FROM historycomment where username=?1 limit 1",nativeQuery = true)
    public Long getId(String username);

    @Modifying
    @Transactional
    @Query(value = "Delete from historycomment where id=?1",nativeQuery = true)
    public Integer deleteHistoryById(Long id);

    @Modifying
    @Transactional
    @Query(value = "Delete from historycomment where username=?1",nativeQuery = true)
    public Integer deleteHistoryByUsername(String username);


    @Query(value = "SELECT * FROM historycomment where username=?1 limit 1",nativeQuery = true)
    public HistoryComment getHistoryCmtByUsername(String username);


    @Query(value = "SELECT id FROM AccPremium.historycomment where running=0 and status=1 and vps=?1 and (UNIX_TIMESTAMP()-task_time/1000)>0 and round((UNIX_TIMESTAMP()-done_time/1000)/60)>=30 order by timeget asc,rand() limit 1;",nativeQuery = true)
    public Long getAccToCmtNoCheckProxy(String vps);

    @Query(value = "SELECT id FROM AccPremium.historycomment where running=0 and vps=?1 and geo=?2 and status=1 and (UNIX_TIMESTAMP()-task_time/1000)>0 and round((UNIX_TIMESTAMP()-done_time/1000)/60)>=30 order by timeget asc,rand() limit 1;",nativeQuery = true)
    public Long getAccToCmtNoCheckProxy_By_Geo(String vps,String geo);

    @Modifying
    @Transactional
    @Query(value = "update historycomment set running=0 where round((UNIX_TIMESTAMP()-timeget/1000)/60)>=40 and running=1",nativeQuery = true)
    public Integer resetThreadThan15mcron();

    @Modifying
    @Transactional
    @Query(value = "UPDATE historycomment SET running=0,videoid='',orderid=0 where vps=?1",nativeQuery = true)
    public Integer resetThreadViewByVps(String vps);

    @Modifying
    @Transactional
    @Query(value = "UPDATE historycomment SET running=0,videoid='',orderid=0 where id=?1",nativeQuery = true)
    public Integer resetThreadBuffhById(Long id);


    @Modifying
    @Transactional
    @Query(value = "update historycomment set status=1 where status=0 and username in (SELECT username FROM AccPremium.account where running=1 and google_suite in (select id from google_suite where state=1)) order by rand() limit 500",nativeQuery = true)
    public Integer resetStatusAccount();

    @Modifying
    @Transactional
    @Query(value = "update historycomment set listvideo=CONCAT(listvideo,\",\",?1),,task_count=task_count+1 where id=?2",nativeQuery = true)
    public Integer updateListVideo(String videoid,Long id);

    @Modifying
    @Transactional
    @Query(value = "update historycomment set listvideo=?1,task_count=task_count+1 where id=?2",nativeQuery = true)
    public Integer updateListVideoNew(String videoid,Long id);
}
