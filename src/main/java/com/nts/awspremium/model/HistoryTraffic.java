package com.nts.awspremium.model;

import javax.persistence.*;

@Entity
@Table(name = "historytraffic")
public class HistoryTraffic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String listorderid;
    private String vps;
    private Integer running;
    private String device;
    private Long timeget;
    @Column(columnDefinition = "bigint default 0")
    private Long task_time=0L;
    private String geo;
    private Long orderid;

    public HistoryTraffic() {
    }

    public HistoryTraffic(Long id, String username, String listorderid, String vps, Integer running, String device, Long timeget, Long task_time, String geo, Long orderid) {
        this.id = id;
        this.username = username;
        this.listorderid = listorderid;
        this.vps = vps;
        this.running = running;
        this.device = device;
        this.timeget = timeget;
        this.task_time = task_time;
        this.geo = geo;
        this.orderid = orderid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getListorderid() {
        return listorderid;
    }

    public void setListorderid(String listorderid) {
        this.listorderid = listorderid;
    }

    public String getVps() {
        return vps;
    }

    public void setVps(String vps) {
        this.vps = vps;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Long getTimeget() {
        return timeget;
    }

    public void setTimeget(Long timeget) {
        this.timeget = timeget;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Long getTask_time() {
        return task_time;
    }

    public void setTask_time(Long task_time) {
        this.task_time = task_time;
    }
}