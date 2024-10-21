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
    private Integer running=0;

    public FingerprintsPC() {
    }

    public FingerprintsPC(Long id, Long get_time, String code, Integer running) {
        this.id = id;
        this.get_time = get_time;
        this.code = code;
        this.running = running;
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

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }
}