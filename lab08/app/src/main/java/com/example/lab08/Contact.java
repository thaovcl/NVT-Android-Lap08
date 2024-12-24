package com.example.lab08;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Contact implements Serializable {
    private int ma;
    private String ten;
    private  String dt;

    public Contact(){

    }

    public Contact(int ma, String ten, String dt) {
        this.ma = ma;
        this.ten = ten;
        this.dt = dt;
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    @NonNull
    @Override
    public String toString() {
        return ma+"-"+ten+"-"+dt;
    }

}
