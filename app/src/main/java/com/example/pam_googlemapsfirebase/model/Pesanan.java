package com.example.pam_googlemapsfirebase.model;

public class Pesanan {
    private String id, tujuan, titikawal, name;

    public Pesanan() {

    }

    public Pesanan(String id, String tujuan, String titikawal, String name) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
