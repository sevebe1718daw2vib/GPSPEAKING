package com.example.gpspeaking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.internal.firebase_auth.zzeu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzv;
import com.google.firebase.auth.zzx;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import static com.google.firebase.auth.FirebaseAuth.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    public EditText txtCorreo, txtContrasena, txtUsername;
    public String correo, contrasena, name;
    public Button btnRegistrar;
    public FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private boolean ContUser;
    private FirebaseDatabase data;
    private DatabaseReference usuarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtContrasena = (EditText) findViewById(R.id.txtContrasena);
        txtUsername = (EditText) findViewById(R.id.txtUsername);


        usuarios= FirebaseDatabase.getInstance().getReference();


        btnRegistrar = (Button) findViewById(R.id.btnLogear);

        firebaseAuth = getInstance();

        btnRegistrar.setOnClickListener(this);

        Toast.makeText(LoginActivity.this, "Introduzca un usuario para acceder si se ha registrado " +
                "sino complete las tres opciones", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnLogear) {

            correo = txtCorreo.getText().toString().trim();
            contrasena = txtContrasena.getText().toString().trim();
            name = txtUsername.getText().toString();

            if(name != null && TextUtils.isEmpty(correo) && TextUtils.isEmpty(contrasena) ) {


                usuarios.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot element : dataSnapshot.getChildren()) {

                                //Toast.makeText(LoginActivity.this,element.child("users").getKey().toString(), Toast.LENGTH_SHORT).show();



                           if (element.child("users").child(name).getKey().equalsIgnoreCase(name)) {

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                               i.putExtra("username", name);
                                startActivity(i);

                                Toast.makeText(LoginActivity.this, "Welcome to the chat of the BPI, User is located", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(LoginActivity.this, "Complete las opciones de registro ", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });






            }else{
/*
                if (TextUtils.isEmpty(correo) && TextUtils.isEmpty(name)) {
                    Toast.makeText(LoginActivity.this, "Debería introducir un email", Toast.LENGTH_SHORT).show();
                    return;
                }*/


                firebaseAuth.createUserWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.signInWithEmailAndPassword(correo, contrasena);

                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                    DatabaseReference currentUser = mDatabase.child(firebaseAuth.getCurrentUser().getUid());
                                    currentUser.child("name").setValue(name);


                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.putExtra("username", name);
                                    startActivity(i);
                                    finish();


                                    Toast.makeText(LoginActivity.this, "El usuario se registro con éxito", Toast.LENGTH_SHORT).show();
                                } else {

                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                        Toast.makeText(LoginActivity.this, "El usuario ya existe, bienvenido a GPSPEAKING, introduzca solo su usuario", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        i.putExtra("username", name);
                                        startActivity(i);
                                        finish();


                                    } else {
                                        Toast.makeText(LoginActivity.this, "El usuario no se registro.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });

                authListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            Toast.makeText(LoginActivity.this, "El usuario se logueo con éxito", Toast.LENGTH_SHORT).show();

                        }
                    }
                };


            }
        }

    }

}