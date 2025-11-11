package com.nts.awspremium.repositories;


import com.nts.awspremium.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoCheckRepository extends JpaRepository<VideoCheck,Long> {
    @Query(value = "SELECT count(*) from video_check  where video_id=?1",nativeQuery = true)
    public Integer checkVideoByVideoId(String video_id);

}
