package com.example.gpspeaking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public Button btnRec;
    public Button btnPlay;
    public Button btnLogin;
    public Button btnMaps;


    private CircleImageView foto;
    private CircleImageView botonCentral;
    private boolean contUser;
    private int cont;
    private String use;
    private List<Audio> listUserAudio = new ArrayList<>();
    FirebaseUser user;




    private static final String LOG_TAG = "AudioRecordTest";
    public static final int RC_RECORD_AUDIO = 1000;
    public static String sRecordedFileName;
    private String hora;
    private String username;
    private static final int PHOTO_PERFIL = 1;
    String fotoPerfil;
    private DatabaseReference databaseReference;
    private String CadenaFoto;
    public FirebaseAuth firebaseAuth;




    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private MediaPlayer mPlayerUrl;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference mEstorage;
    private StorageReference storageReference;
    private Uri u;
    private ImageView image;
    private String fotoNube;
    private Audio a;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent uname = getIntent();
        username = uname.getStringExtra("username");
        contUser = uname.getBooleanExtra("user",true);




        //Toast.makeText(MainActivity.this, use+"   jjjj", Toast.LENGTH_SHORT).show();

        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference().child("users");
        //ChildEventListener us = (ChildEventListener) dbAudios.addChildEventListener("ssss");


        foto = (CircleImageView) findViewById(R.id.fotoPerfil);
        //fotoPerfil="";
        //Toast.makeText(MainActivity.this, use, Toast.LENGTH_SHORT).show();
        botonCentral = (CircleImageView) findViewById(R.id.fotoCentro);
        btnMaps = (Button) findViewById(R.id.btnMap);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(MainActivity.this,user.getEmail().toString(), Toast.LENGTH_SHORT).show();



            //uploadAudio();
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)


                String providerId = profile.getProviderId();



                // UID specific to the provider
                String uid = profile.getUid();


                // Name, email address, and profile photo Url

                String name = profile.getDisplayName();

                String email = profile.getEmail();



                //Uri photoUrl = profile.getPhotoUrl();
            }

        } else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            Toast.makeText(MainActivity.this, "No se ha iniciado sesion", Toast.LENGTH_SHORT).show();

        }



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED){
            Log.i("Permisos", "Se tienen los permisos!");
        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO }, 1222);
        }

        database = FirebaseDatabase.getInstance();
        //storage = FirebaseStorage.getInstance();

        //btnRec = (Button)findViewById(R.id.btnRec);
        btnPlay = (Button)findViewById(R.id.btnUsers);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnPlay.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnMaps.setOnClickListener(this);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        botonCentral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LOG_TAG, "onTouch: " + event.getAction());

                sRecordedFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.3gp";
                 u = Uri.fromFile(new File(sRecordedFileName));
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();

                    playAudio();


                    dbAudios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot element :  dataSnapshot.getChildren()) {

                                use = element.getValue().toString();

                                uploadAudio();
                                }


                            }



                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





                    //send();
                }
                return true;
            }
        });
/*
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });*/


    }

    private String usernameUid(FirebaseUser userUid) {



        userUid = FirebaseAuth.getInstance().getCurrentUser();

        if (userUid != null) {
            Toast.makeText(MainActivity.this, user.getEmail().toString(), Toast.LENGTH_SHORT).show();


            //uploadAudio();
            for (UserInfo profile : userUid.getProviderData()) {
                // Id of the provider (ex: google.com)
/*
                String providerId = profile.getProviderId();
*/

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                /*String name = profile.getDisplayName();

                String email = profile.getEmail();*/


                //Uri photoUrl = profile.getPhotoUrl();
            }

        } else {
            /*Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);*/
            Toast.makeText(MainActivity.this, "No se ha iniciado sesion", Toast.LENGTH_SHORT).show();
        }
     return userUid.getUid();
    }




    private String username(FirebaseUser user) {

        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference().child("users");


        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Toast.makeText(MainActivity.this,user.getEmail().toString(), Toast.LENGTH_SHORT).show();



            //uploadAudio();
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
/*
                String providerId = profile.getProviderId();
*/

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                /*String name = profile.getDisplayName();

                String email = profile.getEmail();*/


                //Uri photoUrl = profile.getPhotoUrl();
            }

        } else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            Toast.makeText(MainActivity.this, "No se ha iniciado sesion", Toast.LENGTH_SHORT).show();

        }

        dbAudios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot element :  dataSnapshot.getChildren()) {

                    use = element.getValue().toString();
                    //uploadAudio(use);
                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return use;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        try{
            if (id == R.id.mChat) {
                Intent intent = new Intent(this, AudioChat.class);
                startActivity(intent);
                return true;
            }
        }catch(Exception e){

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.btnPlay){
            fetchAudioUrlFirebase();
        }else{
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        }*/
    }
    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(sRecordedFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }


    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void playAudio() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(sRecordedFileName);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "onClosing: dudation in millis: " + mPlayer.getDuration());

        mPlayer.start();
    }

    private void uploadAudio() {

        hora = hora();

        DatabaseReference dbAudiofoto = FirebaseDatabase.getInstance().getReference().child("users").child("fotos");
        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference().child("users").child("audios");

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        String fotita = perfil();

        hora = hora();


        //horaAdapter();
        Intent fto = getIntent();



        dbAudiofoto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot element :  dataSnapshot.getChildren()) {

                       if(element.getKey().toString().equalsIgnoreCase(use))

                         fotoNube=element.getValue().toString();

                           a=new Audio(fotoNube,use,"1",hora);
                           dbAudios.push().setValue(a);


                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        //String fotoPrueba = ;
        // = new Audio(fotoNube,use,"1",hora);
        //dbAudios.push().setValue(a);



        //audioChat(a);





        StorageReference file  = storageRef.child("audioChat").child(hora());

        //horaAdapter();


        final ProgressDialog progressDialog=new ProgressDialog(this);

        progressDialog.setMessage("Uploading Audio...");//declare it globally and initialize it with passing the current activity i.e this
        progressDialog.show();


        Uri u = Uri.fromFile(new File(sRecordedFileName));

        file.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progressDialog.dismiss();

            }
        });


    }




   public String hora(){
        Date d = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a dd-MM-yyyy");//a pm o am
        String h = sdf.format(d);

        return h;
    }

    private void fetchAudioUrlFirebase() {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/proyecto-11832.appspot.com/o/audioChat%2FWed%20May%2008%2015%3A30%3A14%20GMT%2B02%3A00%202019?alt=media&token=c5a580e7-2146-442a-be17-e2e0ff5758b5");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    mPlayerUrl = new MediaPlayer();
                    // Download url of file
                    final String url = uri.toString();
                    mPlayerUrl.setDataSource(url);


                    mPlayerUrl.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();

                        }
                    });



                    // wait for media player to get prepare
                    mPlayerUrl.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public String perfil() {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            storageReference = storage.getReference();//imagenes_chat
            StorageReference storageFile = storageReference.child("foto");
            final StorageReference fotoReferencia = storageFile.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            CadenaFoto = uri.toString();
                        }
                    });


                }
            });
        return CadenaFoto;
        }

    /*public void horaAdapter(){
        Intent i = new Intent(MainActivity.this, AudioChat.class);
        i.putExtra("hora", hora);
        startActivity(i);
        finish();
    }*/






    /*public Audio audioChat(Audio a){
        Intent i = new Intent(MainActivity.this, AudioChat.class);
        i.putExtra("username",a.username);
        i.putExtra("hora",a.hora);
        startActivity(i);
        finish();
        return a


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            Uri u = data.getData();

            mEstorage = storage.getReference();//imagenes_chat
            StorageReference storageFile = mEstorage.child("foto");
            final StorageReference fotoReferencia = storageFile.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            fotoPerfil = uri.toString();
                            AudioFrontend m = new AudioFrontend(fotoPerfilCadena,name,String.valueOf(PHOTO_PERFIL),ServerValue.TIMESTAMP);
                            dbAudios.child("foto").setValue(m);
                            Glide.with(MainActivity.this).load(uri.toString()).into(foto);
                        }
                    });


                }
            });
        }
    }
*/
}




