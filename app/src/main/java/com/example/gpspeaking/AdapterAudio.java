package com.example.gpspeaking;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class AdapterAudio extends RecyclerView.Adapter<HolderAudio> implements View.OnClickListener{
    private List<Audio> listAudio = new ArrayList<>();
    private Context c;
    Intent i;
    private DatabaseReference databaseReference;
    String codigoHora;
    private StorageReference storageReference;
    private int pos;
    private MediaPlayer mPlayerUrl;
    private View.OnClickListener listener;




    public AdapterAudio(Context c,List<Audio> listAudio,DatabaseReference databaseReference) {
        this.listAudio=listAudio;
        this.c = c;
        this.databaseReference=databaseReference;

    }


    public void addAudio(Audio a){

        listAudio.add(a);
        notifyItemInserted(listAudio.size());

    }


    @NonNull
    @Override
    public HolderAudio onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(c).inflate(R.layout.perfil,parent,false);
        v.setOnClickListener(this);
        return new HolderAudio(v);

    }


    @Override
    public void onBindViewHolder(@NonNull HolderAudio holder, final int position) {
        holder.getUsername().setText(listAudio.get(position).getUsername());
        //holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        if(listAudio.get(position).getIdFoto().equals("1")) {
            holder.getFotoAudioPerfil().setVisibility(View.VISIBLE);
            Glide.with(c).load(listAudio.get(position).getIdFoto()).into(holder.getFotoAudioPerfil());
        }
        if(listAudio.get(position).getFoto().isEmpty()){
            //holder.getFotoAudioPerfil().setImageResource(R.mipmap.ic_launcher);
            Glide.with(c).load(listAudio.get(position).getFoto()).into(holder.getFotoAudioPerfil());
        }else{
            Glide.with(c).load(listAudio.get(position).getFoto()).into(holder.getFotoAudioPerfil());
        }
         codigoHora = listAudio.get(position).getHora();
        //Long s =Long.valueOf()codigoHora;
        //Date d = new Date(codigoHora);
        //SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a dd-MM-yyyy");//a pm o am
        holder.getHora().setText(codigoHora);


        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String codeHora = listAudio.get(position).getHora();
                Toast.makeText(c, codeHora, Toast.LENGTH_SHORT).show();

                FirebaseStorage storage = FirebaseStorage.getInstance();

                ;//imagenes_chat
                storageReference = storage.getReference("audioChat").child(codeHora);
                //String audio = storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

                //Toast.makeText(c, audio, Toast.LENGTH_SHORT).show();


            }

        });
    }







    @Override
    public int getItemCount() {
        return listAudio.size();

    }

    @Override
    public void onClick(View v) {





    }


    public void setOnClickListener(View.OnClickListener onClickListener) {

    }
}
