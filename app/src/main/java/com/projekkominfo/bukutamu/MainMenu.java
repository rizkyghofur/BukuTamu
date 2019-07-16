package com.projekkominfo.bukutamu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainMenu extends AppCompatActivity {

    EditText etNik, etNama, etAlamat, etTujuan, etPihak;
    TextView etTanggal;
    Button etSubmit, etGambar;
    DatabaseReference mFirebaseDatabase;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mdformat = new SimpleDateFormat("EEEE, dd MMMM yyyy ");
    String strDate = mdformat.format(calendar.getTime());

    Intent intent;
    Uri fileUri;
    ImageView imageView;
    Bitmap bitmap, decoded;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;

    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;

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
        imageView = findViewById(R.id.image_view);

        etTanggal.setText(strDate);

        etSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahdata();
            }
        });

        etGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        imageView.setImageResource(0);
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setTitle("Add Photo!");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e("CAMERA", fileUri.getPath());

                    bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(MainMenu.this.getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setToImageView(Bitmap bmp) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        imageView.setImageBitmap(decoded);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_DeKa_" + timeStamp + ".jpg");

        return mediaFile;
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