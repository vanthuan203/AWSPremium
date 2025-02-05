package com.nts.awspremium.model;

import javax.persistence.*;

@Table(name = "historycomment")
@Entity
public class HistoryComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String listvideo;
    private String vps;
    private Integer running;
    private Long timeget;
    @Column(columnDefinition = "bigint default 0")
    private Long task_time=0L;
    private String geo;
    private String videoid;
    private Long orderid;

    public HistoryComment() {
    }

    public HistoryComment(Long id, String username, String listvideo, String vps, Integer running, Long timeget, Long task_time, String geo, String videoid, Long orderid) {
        this.id = id;
        this.username = username;
        this.listvideo = listvideo;
        this.vps = vps;
        this.running = running;
        this.timeget = timeget;
        this.task_time = task_time;
        this.geo = geo;
        this.videoid = videoid;
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

    public String getListvideo() {
        return listvideo;
    }

    public void setListvideo(String listvideo) {
        this.listvideo = listvideo;
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

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
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
