package com.example.gpspeaking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {
    private GoogleMap mMap;
    private final int PERMIS_LOCALITZACIO_PRECISA = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Task<Location> fusedLocationl;

    private CameraPosition camPos;
    private GoogleApiClient apiClient;
    private int PETICION_PERMISO_LOCALIZACION;
    private Location locationCoor;
    private String usuariofinal;

    FirebaseUser user;
    private String use;
    private String usuarioId;


    LatLng tgn;
    Marker M;


    private Double log;
    private Double lat;
    private String userLatLong;
    private LocationRequest locRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Updategps updateCoordenadas = new Updategps();
        //updateCoordenadas.onLocationChanged(locationCoor);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userLatLong = username(user);




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        setContentView(R.layout.activity_maps2);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        username(user);


        updateLocation();



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference().child("users");
        final DatabaseReference dbAudiosLocation = FirebaseDatabase.getInstance().getReference().child("users").child("target");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationl = fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location loc) {
                        // Got last known location. In some rare situations this can be null.
                        if (loc != null) {

                            log = loc.getLatitude();
                            lat = loc.getLongitude();

                            Audio audio = new Audio(lat.doubleValue(),log.doubleValue());
                            dbAudios.child("target").child(username(user)).setValue(audio);





                            dbAudiosLocation.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(final DataSnapshot element : dataSnapshot.getChildren()){

                                        usuarioId=element.getKey().toString();


                                               /* element.child("latitud").getValue();
                                                element.child("longitud").getValue();*/
                                        Toast.makeText(MapsActivity.this, usuarioId, Toast.LENGTH_SHORT).show();


                                        tgn = new LatLng(Double.valueOf(element.child("longitud").getValue().toString()), Double.valueOf(element.child("latitud").getValue().toString()));


                                       ;


                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        camPos = new CameraPosition.Builder()
                                                .target(tgn)   // Centramos el mapa
                                                .zoom(20)       // Establecemos el zoom
                                                .bearing(0)     // Establecemos la orientación al Noroeste
                                                .tilt(70)       // Bajamos el punto de vista de la camara 70 grados
                                                .build();
                                        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);

                                        mMap.animateCamera(camUpdate);

                                        if(M != null){
                                            M.remove();
                                        }

                                        afegirMarcador(tgn, usuarioId);



                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);


        habilitaLocalitzacio();
    }

    // Afegim el marcador al mapa
    private void afegirMarcador(LatLng latitudLongitud, String titol) {
        // Possibles colors: HUE_RED, HUE_AZURE, HUE_BLUE, HUE_CYAN, HUE_GREEN, HUE_MAGENTA, HUE_ORANGEHUE_ROSE, HUE_VIOLET, HUE_YELLOW


           M= mMap.addMarker(new MarkerOptions().position(latitudLongitud).title(titol)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }

    // Mètode de la interfície GoogleMap.OnMapClickListener
    @Override
    public void onMapClick(LatLng latLng) {
        Projection proj = mMap.getProjection();
        Point coord = proj.toScreenLocation(latLng);

        Toast.makeText(MapsActivity.this,
                "Click\n" + "Lat: " + latLng.latitude +
                        "\n" + "Lng: " + latLng.longitude +
                        "\n" + "X: " + coord.x +
                        ", Y: " + coord.y,
                Toast.LENGTH_SHORT).show();
    }

    // Mètode implementat de la interfície GoogleMap.OnMapLongClickListener
    @Override
    public void onMapLongClick(LatLng latLng) {
        Projection proj = mMap.getProjection();
        Point coord = proj.toScreenLocation(latLng);

        Toast.makeText(MapsActivity.this,
                "Long click\n" + "Lat: " + latLng.latitude + "\n" + "Lng: " + latLng.longitude + "\n" + "X: " + coord.x + ", Y: " + coord.y,
                Toast.LENGTH_SHORT).show();
    }

    // Mètode que demana permisos de localització a l'usuari
    public void habilitaLocalitzacio() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Demanem a l'usuari que ens doni permís per localitzar-se a ell mateix
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMIS_LOCALITZACIO_PRECISA);
        }
    }

    // Per recollir el valor triat per l'usuari sobre els permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Comprovem el codi de permís que ens interessa
        switch (requestCode) {
            case PERMIS_LOCALITZACIO_PRECISA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si ens donen permís per localitzar-nos, ens localitzem
                    habilitaLocalitzacio();

                    @SuppressWarnings("MissingPermission")
                    Task<Location> lastLocation =
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                lat = location.getLatitude();
                                                log = location.getLongitude();
                                            }
                                        }
                                    });

                    //updateUI();

                } else {
                    // Si no tenim permís, podríem fer una altra cosa (sense insultar l'usuari)
                }
            }
        }
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {
           /* Location lastLocation =
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        log = location.getLongitude();
                                    }
                                }
                            });;*/


        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference().child("users");
        final DatabaseReference dbAudiosLocation = FirebaseDatabase.getInstance().getReference().child("users").child("target");



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationl = fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location loc) {
                        // Got last known location. In some rare situations this can be null.
                        if (loc != null) {

                            log = loc.getLatitude();
                            lat = loc.getLongitude();

                            Audio audio = new Audio(lat.doubleValue(),log.doubleValue());
                            dbAudios.child("target").child(username(user)).setValue(audio);





                            dbAudiosLocation.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(final DataSnapshot element : dataSnapshot.getChildren()){



                                        usuarioId = element.getKey().toString();
                                        Toast.makeText(MapsActivity.this, usuarioId, Toast.LENGTH_SHORT).show();


                                               /* element.child("latitud").getValue();
                                                element.child("longitud").getValue();*/


                                        tgn = new LatLng(Double.valueOf(element.child("longitud").getValue().toString()), Double.valueOf(element.child("latitud").getValue().toString()));
                                        M.remove();




                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        camPos = new CameraPosition.Builder()
                                                .target(tgn)   // Centramos el mapa
                                                .zoom(20)       // Establecemos el zoom
                                                .bearing(0)     // Establecemos la orientación al Noroeste
                                                .tilt(70)       // Bajamos el punto de vista de la camara 70 grados
                                                .build();
                                        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);

                                        mMap.animateCamera(camUpdate);

                                        if(M !=null){
                                            M.remove();
                                        }
                                        afegirMarcador(tgn, usuarioId);


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);


        habilitaLocalitzacio();

    }




    // Metodo para establecer el usuario
    private String username(FirebaseUser user) {

        final DatabaseReference dbAudios = FirebaseDatabase.getInstance().getReference().child("users");




        if (user != null) {



            for (UserInfo profile : user.getProviderData()) {

                // UID specific to the provider
                String uid = profile.getUid();

            }

        } else {

            Toast.makeText(MapsActivity.this, "No se ha iniciado sesion", Toast.LENGTH_SHORT).show();

        }

        dbAudios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot element :  dataSnapshot.getChildren()) {

                    use = element.getValue().toString();

                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return use;
    }


    public void updateLocation(){
        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    }
