package com.projekkominfo.bukutamu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainMenu extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    EditText etNik, etNama, etAlamat, etTujuan, etPihak;
    TextView etTanggal;
    Button etSubmit, etGambar;
    Uri image_uri;
    DatabaseReference mFirebaseDatabase;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mdformat = new SimpleDateFormat("EEEE, dd MMMM yyyy ");
    String strDate = mdformat.format(calendar.getTime());
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etTanggal = findViewById(R.id.tanggal);
        etPihak = findViewById(R.id.etPihak);
        etTujuan = findViewById(R.id.etTujuan);
        etSubmit = findViewById(R.id.submit);
        etGambar = findViewById(R.id.gambar);
        ivImage = findViewById(R.id.image_view);
        etTanggal.setText(strDate);

        etGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    //system os < marshmallow
                    openCamera();
                }
            }
        });

    etSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahdata();
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Scam Surat");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Dari kamera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                }
                else {
                    //permission from popup was denied
                    Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera

        if (resultCode == RESULT_OK){
            //set the image captured to our ImageView
            ivImage.setImageURI(image_uri);
        }
    }

    private void tambahdata() {

        String nik = etNik.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String tanggal = etTanggal.getText().toString().trim();
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
            Data datamasuk = new Data(id, nik, nama, alamat, tanggal, pihak, tujuan);
            mFirebaseDatabase.child(id).setValue(datamasuk);
            etNik.setText("");
            etNama.setText("");
            etAlamat.setText("");
            etTanggal.setText(strDate);
            etPihak.setText("");
            etTujuan.setText("");
            Toast.makeText(this,"Data berhasil diinput",Toast.LENGTH_LONG).show();
        }
    }

}