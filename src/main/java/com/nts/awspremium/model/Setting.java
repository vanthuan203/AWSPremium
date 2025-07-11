package com.nts.awspremium.model;

import javax.persistence.*;

@Entity
@Table(name = "setting")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Float maxorder;
    private Integer maxorderbuffhvn;
    private Integer maxorderbuffhus;
    private Float maxrunningam;
    private Float maxrunningpm;
    private Integer mintimebuff;
    private Integer maxthread;
    private Integer pricerate;
    private Integer bonus;
    private Integer maxordervn;
    private Integer maxorderus;
    private Integer levelthread;

    private Integer max_acc_smm=500;
    private Float leveluser;

    private Integer cmtcountuser;
    private Integer check_threads=0;

    private Integer cmtcountuser_us;

    private Integer cmtcountuser_kr;

    private Integer cmtcountuser_jp;
    private Integer redirect;
    private Integer redirectvn;
    private Integer redirectus;
    private Integer threadmin;
    private Integer randview;
    private Integer limit_vps_reset=50;
    private Integer limit_vps_reset_daily=50;
    private Float thread_rate=1.5F;

    private Integer vps_reset_daily=2;
    private Integer vps_reset=3;
    ;



    public Setting() {
    }

    public Integer getLimit_vps_reset_daily() {
        return limit_vps_reset_daily;
    }

    public void setLimit_vps_reset_daily(Integer limit_vps_reset_daily) {
        this.limit_vps_reset_daily = limit_vps_reset_daily;
    }

    public Integer getMax_acc_smm() {
        return max_acc_smm;
    }

    public void setMax_acc_smm(Integer max_acc_smm) {
        this.max_acc_smm = max_acc_smm;
    }

    public Integer getCmtcountuser_us() {
        return cmtcountuser_us;
    }

    public void setCmtcountuser_us(Integer cmtcountuser_us) {
        this.cmtcountuser_us = cmtcountuser_us;
    }

    public Integer getCmtcountuser_kr() {
        return cmtcountuser_kr;
    }

    public void setCmtcountuser_kr(Integer cmtcountuser_kr) {
        this.cmtcountuser_kr = cmtcountuser_kr;
    }

    public Float getMaxrunningam() {
        return maxrunningam;
    }

    public void setMaxrunningam(Float maxrunningam) {
        this.maxrunningam = maxrunningam;
    }

    public Float getMaxrunningpm() {
        return maxrunningpm;
    }

    public void setMaxrunningpm(Float maxrunningpm) {
        this.maxrunningpm = maxrunningpm;
    }

    public Integer getLevelthread() {
        return levelthread;
    }

    public void setLevelthread(Integer levelthread) {
        this.levelthread = levelthread;
    }

    public Integer getMaxordervn() {
        return maxordervn;
    }

    public void setMaxordervn(Integer maxordervn) {
        this.maxordervn = maxordervn;
    }

    public Integer getMaxorderus() {
        return maxorderus;
    }

    public void setMaxorderus(Integer maxorderus) {
        this.maxorderus = maxorderus;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getMaxorder() {
        return maxorder;
    }

    public void setMaxorder(Float maxorder) {
        this.maxorder = maxorder;
    }

    public Integer getMintimebuff() {
        return mintimebuff;
    }

    public void setMintimebuff(Integer mintimebuff) {
        this.mintimebuff = mintimebuff;
    }

    public Integer getMaxthread() {
        return maxthread;
    }

    public void setMaxthread(Integer maxthread) {
        this.maxthread = maxthread;
    }

    public Integer getPricerate() {
        return pricerate;
    }

    public void setPricerate(Integer pricerate) {
        this.pricerate = pricerate;
    }

    public Float getLeveluser() {
        return leveluser;
    }

    public void setLeveluser(Float leveluser) {
        this.leveluser = leveluser;
    }

    public Integer getCmtcountuser() {
        return cmtcountuser;
    }

    public Integer getRedirect() {
        return redirect;
    }

    public void setRedirect(Integer redirect) {
        this.redirect = redirect;
    }

    public Integer getThreadmin() {
        return threadmin;
    }

    public void setThreadmin(Integer threadmin) {
        this.threadmin = threadmin;
    }

    public void setCmtcountuser(Integer cmtcountuser) {
        this.cmtcountuser = cmtcountuser;
    }

    public Integer getMaxorderbuffhvn() {
        return maxorderbuffhvn;
    }

    public void setMaxorderbuffhvn(Integer maxorderbuffhvn) {
        this.maxorderbuffhvn = maxorderbuffhvn;
    }

    public Integer getMaxorderbuffhus() {
        return maxorderbuffhus;
    }

    public void setMaxorderbuffhus(Integer maxorderbuffhus) {
        this.maxorderbuffhus = maxorderbuffhus;
    }

    public Integer getRedirectvn() {
        return redirectvn;
    }

    public void setRedirectvn(Integer redirectvn) {
        this.redirectvn = redirectvn;
    }

    public Integer getRedirectus() {
        return redirectus;
    }

    public void setRedirectus(Integer redirectus) {
        this.redirectus = redirectus;
    }

    public Integer getRandview() {
        return randview;
    }

    public void setRandview(Integer randview) {
        this.randview = randview;
    }

    public Float getThread_rate() {
        return thread_rate;
    }

    public void setThread_rate(Float thread_rate) {
        this.thread_rate = thread_rate;
    }

    public Integer getLimit_vps_reset() {
        return limit_vps_reset;
    }

    public void setLimit_vps_reset(Integer limit_vps_reset) {
        this.limit_vps_reset = limit_vps_reset;
    }


    public Integer getCheck_threads() {
        return check_threads;
    }

    public void setCheck_threads(Integer check_threads) {
        this.check_threads = check_threads;
    }

    public Integer getCmtcountuser_jp() {
        return cmtcountuser_jp;
    }

    public void setCmtcountuser_jp(Integer cmtcountuser_jp) {
        this.cmtcountuser_jp = cmtcountuser_jp;
    }

    public Integer getVps_reset_daily() {
        return vps_reset_daily;
    }

    public void setVps_reset_daily(Integer vps_reset_daily) {
        this.vps_reset_daily = vps_reset_daily;
    }

    public Integer getVps_reset() {
        return vps_reset;
    }

    public void setVps_reset(Integer vps_reset) {
        this.vps_reset = vps_reset;
    }
}
