package com.nts.awspremium.repositories;

import com.nts.awspremium.model.GoogleAPIKey;
import com.nts.awspremium.model.OpenAiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OpenAiKeyRepository extends JpaRepository<OpenAiKey,Long> {

    @Query(value = "Select open_ai_key from open_ai_key where id=1",nativeQuery = true)
    public String get_OpenAI_Key();

    @Query(value = "Select open_ai_key from open_ai_key where id=2",nativeQuery = true)
    public String get_OpenAI_Key2();
}
