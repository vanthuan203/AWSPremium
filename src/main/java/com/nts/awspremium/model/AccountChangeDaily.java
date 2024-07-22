package com.nts.awspremium.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "account_change_daily")
public class AccountChangeDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer running=0;
    private Integer done=0;
    private Integer sum=0;
    private Integer time;
    public AccountChangeDaily() {
    }

}
