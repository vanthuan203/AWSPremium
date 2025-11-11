package com.nts.awspremium.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "video_check")
@Setter
@Getter
public class VideoCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(255) default ''")
    private String video_id="";
    @Column(columnDefinition = "float default 0")
    private Float charge;
    @Column(columnDefinition = "varchar(255) default ''")
    private String video_title="";
    @Column(columnDefinition = "varchar(5000) default ''")
    private String video_description="";
    @Column(columnDefinition = "varchar(255) default ''")
    private String task="";
    @Column(columnDefinition = "bigint default 0")
    private Long order_time=0L;
    public VideoCheck() {
    }
}
