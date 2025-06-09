package com.nts.awspremium.model;

public interface BalanceHistory {
    Long getId();
    String getUser();
    Float getBalance();
    Float getTotalblance();
    Long getTime();
    String getNote();
    String getGeo();
    String getTask();
    Integer getAi();
    Integer getLive();
    Integer getService();
}
