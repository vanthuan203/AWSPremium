package com.nts.awspremium.model;

import javax.persistence.*;

@Entity
@Table(name = "videocomment")
public class VideoComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderid;
    private String videoid;
    private String videotitle;
    private String channelid;
    private String channeltitle;
    private Integer commentstart;
    private Long insertdate;
    private Integer maxthreads;
    private Long duration;
    private Integer service;
    private String note;
    private String user;
    private Integer enddate;
    private Integer commentorder;
    private Integer commenttotal;

    private Integer comment_render=0;
    private String listcomment;
    private String description="";
    private String lc_code="";
    private Long timeupdate;
    private Float price;
    private Integer valid;

    @Column(columnDefinition = "varchar(555) default ''")
    private String blocked_list="";

    @Column(columnDefinition = "varchar(555) default ''")
    private String chat_id;

    @Column(columnDefinition = "bigint default 0")
    private Long chat_time;

    public VideoComment() {
    }

    public VideoComment(Long orderid, String videoid, String videotitle, String channelid, String channeltitle, Integer commentstart, Long insertdate, Integer maxthreads, Long duration, Integer service, String note, String user, Integer enddate, Integer commentorder, Integer commenttotal, Integer comment_render, String listcomment, String description, String lc_code, Long timeupdate, Float price, Integer valid, String blocked_list, String chat_id, Long chat_time) {
        this.orderid = orderid;
        this.videoid = videoid;
        this.videotitle = videotitle;
        this.channelid = channelid;
        this.channeltitle = channeltitle;
        this.commentstart = commentstart;
        this.insertdate = insertdate;
        this.maxthreads = maxthreads;
        this.duration = duration;
        this.service = service;
        this.note = note;
        this.user = user;
        this.enddate = enddate;
        this.commentorder = commentorder;
        this.commenttotal = commenttotal;
        this.comment_render = comment_render;
        this.listcomment = listcomment;
        this.description = description;
        this.lc_code = lc_code;
        this.timeupdate = timeupdate;
        this.price = price;
        this.valid = valid;
        this.blocked_list = blocked_list;
        this.chat_id = chat_id;
        this.chat_time = chat_time;
    }

    public String getListcomment() {
        return listcomment;
    }

    public void setListcomment(String listcomment) {
        this.listcomment = listcomment;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getVideotitle() {
        return videotitle;
    }

    public void setVideotitle(String videotitle) {
        this.videotitle = videotitle;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getChanneltitle() {
        return channeltitle;
    }

    public void setChanneltitle(String channeltitle) {
        this.channeltitle = channeltitle;
    }

    public Integer getCommentstart() {
        return commentstart;
    }

    public void setCommentstart(Integer commentstart) {
        this.commentstart = commentstart;
    }

    public Long getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(Long insertdate) {
        this.insertdate = insertdate;
    }

    public Integer getMaxthreads() {
        return maxthreads;
    }

    public void setMaxthreads(Integer maxthreads) {
        this.maxthreads = maxthreads;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer service) {
        this.service = service;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getEnddate() {
        return enddate;
    }

    public void setEnddate(Integer enddate) {
        this.enddate = enddate;
    }

    public Integer getCommentorder() {
        return commentorder;
    }

    public void setCommentorder(Integer commentorder) {
        this.commentorder = commentorder;
    }

    public Integer getCommenttotal() {
        return commenttotal;
    }

    public void setCommenttotal(Integer commenttotal) {
        this.commenttotal = commenttotal;
    }

    public Long getTimeupdate() {
        return timeupdate;
    }

    public void setTimeupdate(Long timeupdate) {
        this.timeupdate = timeupdate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public String getLc_code() {
        return lc_code;
    }

    public void setLc_code(String lc_code) {
        this.lc_code = lc_code;
    }

    public Integer getComment_render() {
        return comment_render;
    }

    public void setComment_render(Integer comment_render) {
        this.comment_render = comment_render;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public Long getChat_time() {
        return chat_time;
    }

    public void setChat_time(Long chat_time) {
        this.chat_time = chat_time;
    }

    public String getBlocked_list() {
        return blocked_list;
    }

    public void setBlocked_list(String blocked_list) {
        this.blocked_list = blocked_list;
    }
}
