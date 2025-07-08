package com.nts.awspremium.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(255) default ''")
    private String username;
    @Column(columnDefinition = "varchar(255) default ''")
    private String password;
    @Column(columnDefinition = "varchar(255) default ''")
    private String recover;
    @Column(columnDefinition = "integer default 1")
    private Integer live=1;
    @Column(columnDefinition = "varchar(255) default ''")
    private String vps="";
    private String proxy="";
    @Column(columnDefinition = "integer default 0")
    private Integer running=0;
    @Column(columnDefinition = "bigint default 0")
    private Long get_time=0L;
    @Column(columnDefinition = "bigint default 0")
    private Long end_time=0L;
    @Column(columnDefinition = "varchar(255) default ''")
    private  String date="";
    @Column(columnDefinition = "varchar(255) default ''")
    private  String google_suite="";
    private String geo;
    @Column(columnDefinition = "TINYINT default 0")
    private Boolean status=false;
    @Column(columnDefinition = "TINYINT default 0")
    private Boolean reg=false;
    @Column(columnDefinition = "varchar(255) default '1'")
    private  String group_mail="1";

    public Account() {
    }
}

