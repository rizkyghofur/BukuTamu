package com.projekkominfo.bukutamu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainMenu extends AppCompatActivity {

    EditText etNik, etNama, etAlamat, etTujuan, etPihak;
    TextView etTanggal;
    Button etSubmit, etGambar;
    DatabaseReference mFirebaseDatabase;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mdformat = new SimpleDateFormat("EEEE, dd MMMM yyyy ");
    String strDate = mdformat.format(calendar.getTime());

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView ivImage;
    private String userChoosenTask;

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
                selectImage();
            }
        });

        etSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahdata();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UtilCam.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Ambil Foto", "Pilih dari Galeri",
                "Batal" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setTitle("Tambah lampiran surat");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=UtilCam.checkPermission(MainMenu.this);

                if (items[item].equals("Ambil Foto")) {
                    userChoosenTask ="Ambil Foto";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Pilih dari Galeri")) {
                    userChoosenTask ="Pilih dari Galeri";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

}