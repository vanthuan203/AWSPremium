package com.nts.awspremium.model;

import javax.persistence.*;

@Entity
@Table(name = "open_ai_key")
public class OpenAiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String open_ai_key;
    private Long count_get;
    private Integer state;

    public OpenAiKey() {
    }

    public OpenAiKey(Long id, String open_ai_key, Long count_get, Integer state) {
        this.id = id;
        this.open_ai_key = open_ai_key;
        this.count_get = count_get;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpen_ai_key() {
        return open_ai_key;
    }

    public void setOpen_ai_key(String open_ai_key) {
        this.open_ai_key = open_ai_key;
    }

    public Long getCount() {
        return count_get;
    }

    public void setCount(Long count_get) {
        this.count_get = count_get;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
