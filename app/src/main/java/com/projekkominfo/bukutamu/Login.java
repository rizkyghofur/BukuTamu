package com.projekkominfo.bukutamu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
        }

        editTextEmail = findViewById(R.id.user);
        editTextPassword = findViewById(R.id.pass);
        buttonSignIn = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        buttonSignIn.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Masukkan email terlebih dahulu.",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Masukkan kata sandi terlebih dahulu.",Toast.LENGTH_LONG).show();
            return;
        }

        if(!Helper.isValidEmail(email)){
            Helper.displayMessageToast(Login.this, "Email salah!");
            return;
        }

        progressDialog.setMessage("Sedang melakukan login, Mohon tunggu");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Login berhasil",Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                        }
                        else{
                            Toast.makeText(Login.this,"Login Error, cek kembali email dan kata sandi Anda",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }
    }
}