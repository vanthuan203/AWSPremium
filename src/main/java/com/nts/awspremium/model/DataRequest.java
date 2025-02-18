package com.nts.awspremium.model;

public class DataRequest {
    private String key;
    private String link;
    private int quantity;
    private String action;
    private int service=-1;
    private int product=-1;
    private Long order=-1L;
    private Long order_refill=-1L;
    private String orders="";
    private String list="";
    private String search="";
    private String suggest="";
    private String comments="";
    private String require="";
    private String note="";
    private String hashtag="";

    public DataRequest() {
    }

    public DataRequest(String key, String link, int quantity, String action, int service, int product, Long order, Long order_refill, String orders, String list, String search, String suggest, String comments, String require, String note, String hashtag) {
        this.key = key;
        this.link = link;
        this.quantity = quantity;
        this.action = action;
        this.service = service;
        this.product = product;
        this.order = order;
        this.order_refill = order_refill;
        this.orders = orders;
        this.list = list;
        this.search = search;
        this.suggest = suggest;
        this.comments = comments;
        this.require = require;
        this.note = note;
        this.hashtag = hashtag;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getKey() {
        return key;
    }



    public void setKey(String key) {
        this.key = key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getOrder_refill() {
        return order_refill;
    }

    public void setOrder_refill(Long order_refill) {
        this.order_refill = order_refill;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
