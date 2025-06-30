package com.nts.awspremium.repositories;

import com.nts.awspremium.model.DataOrder;
import com.nts.awspremium.model.GoogleSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoogleSuiteRepository extends JpaRepository<GoogleSuite,Long> {
}
