package com.nts.awspremium.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fingerprints_pc")
public class FingerprintsPC {
    @Id
    private Long id;
    private Long get_time=0L;
    private String code="";

    public FingerprintsPC() {
    }

    public FingerprintsPC(Long id, Long get_time, String code) {
        this.id = id;
        this.get_time = get_time;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGet_time() {
        return get_time;
    }

    public void setGet_time(Long get_time) {
        this.get_time = get_time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}