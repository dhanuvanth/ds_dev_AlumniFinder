package com.example.aluminifinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double lat;
    private Double log;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationManager locationManager;

    private String college;
    private String district;
    private String state;
    private String currentCollege;
    private String currentDistrict;
    private String currentState;
    private String email;
    private String currentEmail;
    private int MY_PERMISSIONS_REQUEST_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currentEmail = mAuth.getCurrentUser().getEmail();

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
        permission();
        db.collection("Emails").document(mAuth.getCurrentUser().getEmail()).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    try {
                        mMap.clear();
                        Note latlog = documentSnapshot.toObject(Note.class);

                        lat = latlog.getLat();
                        log = latlog.getLng();
                        currentCollege = latlog.getCollege_txt();
                        currentDistrict = latlog.getDistrict_txt();
                        currentState = latlog.getState_txt();
                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(lat, log);
                        String locName = "" + documentSnapshot.get("name1");
                        mMap.addMarker(new MarkerOptions().position(sydney).title(locName).snippet(currentEmail)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                        alumniSearch();
                    } catch (Exception ex) {
                        Toast.makeText(MapsActivity.this, "Refresh map", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void alumniSearch() {
        db.collection("Emails").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (final DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        college = getIntent().getStringExtra("college");
                        district = getIntent().getStringExtra("district");
                        state = getIntent().getStringExtra("state");
                        email = "" + snapshot.get("email");
                        if (!currentEmail.equals(email)) {
                            if (currentCollege.equals(college) && currentDistrict.equals(district) && currentState.equals(state)) {
                                final String latitude = "" + snapshot.get("lat");
                                final String longitude = "" + snapshot.get("lng");

                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                                        .title("" + snapshot.get("name1"))
                                        .snippet(""+snapshot.get("email"))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(final Marker marker) {
                                    if (!(marker.getSnippet().equals(currentEmail))) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Invite").setMessage("" + marker.getTitle())
                                                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(MapsActivity.this, MeetUpActivity.class);
                                                        i.putExtra("email_id",marker.getSnippet());
                                                        i.putExtra("college",college);
                                                        startActivity(i);
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create().show();
                                    }
                                    return false;
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void permission() {
        //check if permission is already granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //program execute when btn is pressed
            permissionGranted();
            statusCheck();
        } else {
            //show the premission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //if permission granted
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted();
                statusCheck();
            }
        }
    }

    private void permissionGranted() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Note note = new Note();
                note.setLat(location.getLatitude());
                note.setLng(location.getLongitude());
                try {
                    db.collection("Emails").document(mAuth.getCurrentUser().getEmail()).update("lat", note.getLat(), "lng", note.getLng());
                } catch (Exception e) {
                    location.reset();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
