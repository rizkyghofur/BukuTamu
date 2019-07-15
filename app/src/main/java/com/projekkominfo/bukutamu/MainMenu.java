package com.projekkominfo.bukutamu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainMenu extends AppCompatActivity {

    EditText etNik, etNama, etAlamat, etTujuan, etPihak;
    Button etSubmit;
    DatabaseReference mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etPihak = findViewById(R.id.etPihak);
        etTujuan = findViewById(R.id.etTujuan);
        etSubmit = findViewById(R.id.submit);

        etSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahdata();
            }
        });
    }

    private void tambahdata() {

        String nik = etNik.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String pihak = etPihak.getText().toString().trim();
        String tujuan = etTujuan.getText().toString().trim();

        if(TextUtils.isEmpty(nik)){
            Toast.makeText(this, "Masukkan NIK terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(nama)){
            Toast.makeText(this,"Masukkan nama terlebih dahulu",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(alamat)){
            Toast.makeText(this,"Masukkan alamat terlebih dahulu",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(pihak)){
            Toast.makeText(this,"Masukkan pihak yang ditemui terlebih dahulu",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(tujuan)){
            Toast.makeText(this,"Masukkan tujuan terlebih dahulu",Toast.LENGTH_LONG).show();
            return;
        } else{
            String id = mFirebaseDatabase.push().getKey();
            Data datamasuk = new Data(id, nik, nama, alamat, pihak, tujuan);
            mFirebaseDatabase.child(id).setValue(datamasuk);
            etNik.setText("");
            etNama.setText("");
            etAlamat.setText("");
            etPihak.setText("");
            etTujuan.setText("");
            Toast.makeText(this,"Data berhasil diinput",Toast.LENGTH_LONG).show();
        }
    }
}