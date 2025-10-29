package com.nts.awspremium.model;

import javax.persistence.*;

@Table(name = "historyview")
@Entity
public class HistoryView {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String listvideo;
    private String proxy;
    private String vps;
    private Integer running;
    private String channelid;
    private Long timeget;
    @Column(columnDefinition = "bigint default 0")
    private Long task_time=0L;
    private String typeproxy;
    private String geo;
    private String geo_rand="";
    private String videoid;
    private Long orderid;
    @Column(columnDefinition = "bigint default 0")
    private Long finger_id=0L;
    @Column(columnDefinition = "integer default 0")
    private Integer max_time=0;

    @Column(columnDefinition = "integer default 0")
    private Integer task_index=0;

    @Column(columnDefinition = "integer default 0")
    private Integer max_task=0;

    @Column(columnDefinition = "integer default 2")
    private Integer channel_index=2;

    @Column(columnDefinition = "TINYINT default 1")
    private Boolean state=true;

    @Column(columnDefinition = "TINYINT default 1")
    private Boolean status=true;

    private Integer task_done=0;

    public HistoryView() {
    }

    public HistoryView(Long id, String username, String listvideo, String proxy, String vps, Integer running, String channelid, Long timeget, Long task_time, String typeproxy, String geo, String geo_rand, String videoid, Long orderid, Long finger_id, Integer max_time, Integer task_done) {
        this.id = id;
        this.username = username;
        this.listvideo = listvideo;
        this.proxy = proxy;
        this.vps = vps;
        this.running = running;
        this.channelid = channelid;
        this.timeget = timeget;
        this.task_time = task_time;
        this.typeproxy = typeproxy;
        this.geo = geo;
        this.geo_rand = geo_rand;
        this.videoid = videoid;
        this.orderid = orderid;
        this.finger_id = finger_id;
        this.max_time = max_time;
        this.task_done = task_done;
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

    public Integer getChannel_index() {
        return channel_index;
    }

    public void setChannel_index(Integer channel_index) {
        this.channel_index = channel_index;
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

    public Integer getTask_done() {
        return task_done;
    }

    public void setTask_done(Integer task_done) {
        this.task_done = task_done;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
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

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
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

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public Long getTimeget() {
        return timeget;
    }

    public void setTimeget(Long timeget) {
        this.timeget = timeget;
    }

    public String getTypeproxy() {
        return typeproxy;
    }

    public void setTypeproxy(String typeproxy) {
        this.typeproxy = typeproxy;
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

    public String getGeo_rand() {
        return geo_rand;
    }

    public void setGeo_rand(String geo_rand) {
        this.geo_rand = geo_rand;
    }

    public Long getFinger_id() {
        return finger_id;
    }

    public void setFinger_id(Long finger_id) {
        this.finger_id = finger_id;
    }

    public Long getTask_time() {
        return task_time;
    }

    public void setTask_time(Long task_time) {
        this.task_time = task_time;
    }

    public Integer getMax_time() {
        return max_time;
    }

    public void setMax_time(Integer max_time) {
        this.max_time = max_time;
    }
}
