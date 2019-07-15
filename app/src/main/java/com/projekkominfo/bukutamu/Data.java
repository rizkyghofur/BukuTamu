package com.projekkominfo.bukutamu;

import com.google.firebase.database.IgnoreExtraProperties;
import java.lang.String;

@IgnoreExtraProperties
public class Data {
    private String id;
    private String nik;
    private String nama;
    private String alamat;
    private String pihak;
    private String tujuan;

    public Data(String id, String nik, String nama, String alamat, String pihak, String tujuan) {
        this.id = id;
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.pihak = pihak;
        this.tujuan = tujuan;
    }
}