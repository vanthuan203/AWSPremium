package com.nts.awspremium.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "product_history")
@Getter
@Setter
public class ProductHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;
    @Column(columnDefinition = "varchar(255) default ''")
    private String uuid;
    @Column(columnDefinition = "varchar(255) default ''")
    private String product_key;
    @Column(columnDefinition = "float default 0")
    private Float charge;
    @Column(columnDefinition = "TEXT")
    private String list_id;
    @Column(columnDefinition = "integer default 0")
    private Integer quantity;
    @Column(columnDefinition = "bigint default 0")
    private Long insert_time;
    @Column(columnDefinition = "integer default 0")
    private Integer cancel;
    @Column(columnDefinition = "integer default 0")
    private Integer service;
    @Column(columnDefinition = "varchar(500) default ''")
    private String note;
    @Column(columnDefinition = "varchar(255) default ''")
    private String user;
    @Column(columnDefinition = "integer default 0")
    private Integer refund=0;
    @Column(columnDefinition = "bigint default 0")
    private Long refund_time;
}
