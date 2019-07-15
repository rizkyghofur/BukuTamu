package com.projekkominfo.bukutamu;

import java.io.Serializable;

public class Requests implements Serializable {

    private String nik;
    private String nama;
    private String alamat;
    private String pihak;
    private String tujuan;
    private String key;

    public Requests(){

    }

    public Requests(String  nik, String nama, String alamat, String pihak, String tujuan){
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.pihak = pihak;
        this.tujuan = tujuan;

    }

    public String  getNik() {return nik;}
    public void setNik(String nik) {this.nik = nama;}
    public String getNama() {return nama;}
    public void setNama(String nama) {this.nama = nama;}
    public String getAlamat() {return alamat;}
    public void setAlamat(String alamat) {this.alamat = alamat;}
    public String getPihak() {return pihak;}
    public void setPihak(String pihak) {this.pihak = pihak;}
    public String getTujuan() {return tujuan;}
    public void setTujuan(String tujuan) {this.tujuan = tujuan;}
    public String getKey() {return key;}
    public void setKey(String key) {this.key = key;}

    @Override
    public String toString () {
        return " "+nik+"\n" +
                " "+nama+"\n" +
                " "+alamat+"\n" +
                " "+pihak+"\n" +
                " "+tujuan;
    }
}
