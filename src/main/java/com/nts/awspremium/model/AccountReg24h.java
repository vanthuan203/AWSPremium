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
@Table(name = "account_reg_24h")
public class AccountReg24h {
    @Id
    private String id;
    @Column(columnDefinition = "TINYINT default 0")
    private Boolean status=false;
    @Column(columnDefinition = "bigint default 0")
    private Long update_time;
}