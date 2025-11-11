package com.nts.awspremium.repositories;


import com.nts.awspremium.model.Admin;
import com.nts.awspremium.model.AutoRefill;
import com.nts.awspremium.model.Service;
import com.nts.awspremium.model.VideoCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoCheckRepository extends JpaRepository<VideoCheck,Long> {

}
