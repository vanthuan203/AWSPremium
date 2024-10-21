package com.nts.awspremium.repositories;


import com.nts.awspremium.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FingerprintsPCRepository extends JpaRepository<FingerprintsPC,Long> {
    @Query(value = "call update_running_finger_pc(?1,?2)",nativeQuery = true)
    public Long get_Finger_PC(Long get_time, String code);
}
