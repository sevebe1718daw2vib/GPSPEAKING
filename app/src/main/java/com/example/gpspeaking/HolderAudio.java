package com.example.gpspeaking;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HolderAudio extends RecyclerView.ViewHolder {

    private TextView username;
    private TextView hora;
    private CircleImageView fotoAudioPerfil;


    public HolderAudio(View itemView) {
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.Username);
        hora = (TextView) itemView.findViewById(R.id.horaAudio);
        fotoAudioPerfil = (CircleImageView) itemView.findViewById(R.id.fotoPerfilAudio);
        //fotoMensaje = (ImageView) itemView.findViewById(R.id.);
    }

    public TextView getUsername() {
        return username;
    }

    public TextView getHora() {
        return hora;
    }

    public CircleImageView getFotoAudioPerfil() {
        return fotoAudioPerfil;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public void setFotoAudioPerfil(CircleImageView fotoAudioPerfil) {
        this.fotoAudioPerfil = fotoAudioPerfil;
    }
}