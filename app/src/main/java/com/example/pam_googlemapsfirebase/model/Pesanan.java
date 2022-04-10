package com.example.pam_googlemapsfirebase.model;

public class Pesanan {
    private String orderId, tujuan, titikawal, name;

    public Pesanan() {

    }

    public Pesanan(String orderId, String tujuan, String titikawal, String name) {
        this.orderId = orderId;
        this.tujuan = tujuan;
        this.titikawal = titikawal;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getTitikawal() {
        return titikawal;
    }

    public void setTitikawal(String titikawal) {
        this.titikawal = titikawal;
    }
}
