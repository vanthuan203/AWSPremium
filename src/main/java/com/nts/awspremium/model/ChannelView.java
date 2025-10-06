package com.nts.awspremium.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "channel_view")
public class ChannelView {
    @Id
    private String channel_id;
    @Column(columnDefinition = "varchar(255) default ''")
    private String channel_title="";
    @Column(columnDefinition = "varchar(15555) default ''")
    private String video_list="";
    @Column(columnDefinition = "bigint default 0")
    private Long order_time=0L;
    @Column(columnDefinition = "bigint default 0")
    private Long update_time=0L;

}