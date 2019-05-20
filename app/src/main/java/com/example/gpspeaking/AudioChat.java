package com.example.gpspeaking;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AudioChat extends AppCompatActivity {

    private AdapterAudio adapter;
    private RecyclerView rvAudio;
    private long hora;

    private CircleImageView foto;
    private static final int PHOTO_PERFIL = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference audioFotos;
    private FirebaseDatabase database;
    private int pos;
    private String usuario;
    //private Uri u;

    private String fotoPerfilCadena;
    private String name;
    private String horaChat;
    private List<Audio> listaAudios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            dbAudios.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot element : dataSnapshot.getChildren()) {

                        usuario = element.getValue().toString();
                        Toast.makeText(AudioChat.this, usuario, Toast.LENGTH_SHORT).show();

                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(AudioChat.this, "no hay naaaaa", Toast.LENGTH_SHORT).show();

        }


        Intent uname = getIntent();
        name = uname.getStringExtra("usermane");
        horaChat = uname.getStringExtra("hora");

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users").child("audios");//Sala de chat (nombre)

        storage = FirebaseStorage.getInstance();

        listaAudios = new ArrayList<Audio>();

        rvAudio = (RecyclerView) findViewById(R.id.rvAudios);
        foto = (CircleImageView) findViewById(R.id.fotoPerfil);
        // databaseReference = database.getReference().child("users").child("audio");
        adapter = new AdapterAudio(this, listaAudios, databaseReference);


        rvAudio.setLayoutManager(new LinearLayoutManager(this));
        rvAudio.setHasFixedSize(true);
        rvAudio.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        rvAudio.setAdapter(adapter);


        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_PERFIL);



            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Audio a = dataSnapshot.getValue(Audio.class);
                adapter.addAudio(a);




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*listaAudios.removeAll(listaAudios);
                // Recorrem tots els elements del DataSnapshot i els mostrem
                for (DataSnapshot element : dataSnapshot.getChildren()) {
                    Audio audio = new Audio(
                            element.child("foto").getValue().toString(),
                            element.child("username").getValue().toString(),
                            element.child("idFoto").getValue().toString(),
                            element.child("hora").getValue().toString());
                    listaAudios.add(audio);
                }

                // Per si hi ha canvis, que es refresqui l'adaptador
                adapter.notifyDataSetChanged();*/
                // rvAudio.scrollToPosition(listaAudios.size() - 1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseStorage storage = FirebaseStorage.getInstance();

                ;//imagenes_chat
                String filename = listaAudios.get(pos).getHora();
                storageReference = storage.getReference("audioChat").child(filename);
                String audio = storageReference.getDownloadUrl().toString();

                Toast.makeText(AudioChat.this, "ssss", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public String hora() {
        Date d = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a dd-MM-yyyy");//a pm o am
        String h = sdf.format(d);

        return h;
    }

    private String username() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_PERFIL && resultCode == RESULT_OK) {

            Uri u = data.getData();


            final DatabaseReference dbAudioss = FirebaseDatabase.getInstance().getReference().child("users").child("fotos");

            storageReference = storage.getReference();//imagenes_cha
            StorageReference storageFile = storageReference.child("foto");
            final StorageReference fotoReferencia = storageFile.child(u.getLastPathSegment());


            fotoReferencia.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            Toast.makeText(AudioChat.this, usuario+" j j jj", Toast.LENGTH_SHORT).show();
                            fotoPerfilCadena=uri.toString();
                            //Audio m = new Audio(fotoPerfilCadena);
                            dbAudioss.child(usuario).setValue(fotoPerfilCadena.toString(),"CAMBIO FOTO PERFIL");



                            Glide.with(AudioChat.this).load(uri.toString()).into(foto);

                        }
                    });


                }
            });


            /*

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
                            fotoPerfilCadena = uri.toString();

                            Audio m = new Audio(fotoPerfilCadena,usuario,"1",hora());
                            databaseReference.push().setValue(m);
                            Glide.with(AudioChat.this).load(uri.toString()).into(foto);

                        }
                    });


                }
            });*/
        }
    }
}

