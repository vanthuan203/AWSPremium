package com.nts.awspremium.model_system;

import org.springframework.stereotype.Component;

@Component
public class MySQLCheck {
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
