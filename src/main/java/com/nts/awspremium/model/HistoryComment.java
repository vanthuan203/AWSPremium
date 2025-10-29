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

    @Column(columnDefinition = "integer default 0")
    private Integer task_index=0;

    @Column(columnDefinition = "integer default 0")
    private Integer max_task=0;

    @Column(columnDefinition = "integer default 0")
    private Integer task_count=0;

    @Column(columnDefinition = "integer default 0")
    private Integer task_false=0;
    @Column(columnDefinition = "integer default 0")
    private Integer task_success=0;

    @Column(columnDefinition = "integer default 1")
    private Integer channel_index=1;

    @Column(columnDefinition = "varchar(255) default ''")
    private String user_id="";
    @Column(columnDefinition = "TINYINT default 1")
    private Boolean state=true;

    @Column(columnDefinition = "TINYINT default 1")
    private Boolean status=true;
    private String geo;
    private String videoid;
    private Long orderid;

    public HistoryComment() {
    }

    public HistoryComment(Long id, String username, String listvideo, String vps, Integer running, Long timeget, Long task_time, Integer task_index, Integer max_task, Integer task_count, Integer channel_index, String geo, String videoid, Long orderid) {
        this.id = id;
        this.username = username;
        this.listvideo = listvideo;
        this.vps = vps;
        this.running = running;
        this.timeget = timeget;
        this.task_time = task_time;
        this.task_index = task_index;
        this.max_task = max_task;
        this.task_count = task_count;
        this.channel_index = channel_index;
        this.geo = geo;
        this.videoid = videoid;
        this.orderid = orderid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
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

    public Integer getTask_count() {
        return task_count;
    }

    public void setTask_count(Integer task_count) {
        this.task_count = task_count;
    }

    public Integer getTask_index() {
        return task_index;
    }

    public void setTask_index(Integer task_index) {
        this.task_index = task_index;
    }

    public Integer getMax_task() {
        return max_task;
    }

    public void setMax_task(Integer max_task) {
        this.max_task = max_task;
    }

    public Integer getChannel_index() {
        return channel_index;
    }

    public void setChannel_index(Integer channel_index) {
        this.channel_index = channel_index;
    }

    public Integer getTask_false() {
        return task_false;
    }

    public void setTask_false(Integer task_false) {
        this.task_false = task_false;
    }

    public Integer getTask_success() {
        return task_success;
    }

    public void setTask_success(Integer task_success) {
        this.task_success = task_success;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
