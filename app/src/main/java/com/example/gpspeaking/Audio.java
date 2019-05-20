package com.example.gpspeaking;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Map;

public class Audio {

    String username;
    String foto;
    String idFoto;
    String hora;
    Double latitud;
    Double longitud;


    public Audio() {

    }

    public Audio(String dato) {
        this.username = username;
    }



    public Audio(Double latitud, Double longitud) {

        this.latitud = latitud;
        this.longitud = longitud;
    }


    public Audio(String foto, String username, String idFoto, String hora) {

        this.foto = foto;
        this.username=username;
        this.idFoto = idFoto;
        this.hora = hora;
    }

    public String getUsername() {
        return username;
    }

    public String getIdFoto() {
        return idFoto;
    }


    public String getFoto() {
        return foto;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
