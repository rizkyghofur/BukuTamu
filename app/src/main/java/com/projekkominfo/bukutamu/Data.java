package com.projekkominfo.bukutamu;

import com.google.firebase.database.IgnoreExtraProperties;
import java.lang.String;

@IgnoreExtraProperties
public class Data {String id;
    String nik;
    String nama;
    String alamat;
    String tanggal;
    String pihak;
    String tujuan;


    public Data(String id, String nik, String nama, String alamat, String tanggal, String pihak, String tujuan) {
        this.id = id;
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.tanggal = tanggal;
        this.pihak = pihak;
        this.tujuan = tujuan;
    }
}