package com.poraq.mobiapp;

public class OrderModel {

    String type , price , Qu , sent ;

    public OrderModel(String type,  String qu, String sent , String price) {
        this.type = type;
        this.price = price;
        Qu = qu;
        this.sent = sent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQu() {
        return Qu;
    }

    public void setQu(String qu) {
        Qu = qu;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }
}
