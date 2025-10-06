package com.nts.awspremium.repositories;

import com.nts.awspremium.model.ChannelBlackList;
import com.nts.awspremium.model.ChannelView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ChannelViewRepository extends JpaRepository<ChannelView,String> {
    @Modifying
    @Transactional
    @Query(value = "delete from channel_view",nativeQuery = true)
    public void deleteAll();

    @Modifying
    @Transactional
    @Query(value = "delete from channel_view where channel_id=?1",nativeQuery = true)
    public void deleteChannelViewById(String channel_id);

    @Query(value = "select count(*) from channel_view where channel_id=?1",nativeQuery = true)
    public Integer getCountByChannelId(String channel_id);

    @Query(value = "select * from channel_view where channel_id=?1 limit 1",nativeQuery = true)
    public ChannelView getChannelByChannelId(String channel_id);

    @Query(value = "SELECT * FROM AccPremium.channel_view where video_list='' order by update_time asc limit ?1",nativeQuery = true)
    public List<ChannelView> getListChannelAddVideoList(Integer limit);

    @Query(value = "select channel_id from (select channel_view.channel_id,count(running) as total\n" +
            "           from channel_view left join historyview on historyview.channelid=channel_view.channel_id and running=1 and orderid=-1 where channel_view.video_list!=''\n" +
            "                  group by channel_view.channel_id having total<?1) as t",nativeQuery = true)
    public List<String> getListChannelTrueThreadON(Integer thread);
}
