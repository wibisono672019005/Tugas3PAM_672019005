package com.example.pam_googlemapsfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pam_googlemapsfirebase.model.Pesanan;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderPesanan extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    //Variabel
    private GoogleMap gMap;
    private Marker selectedMarker;
    private LatLng selectedPlace, LokTer;

    private FirebaseFirestore db;

    private TextView txtOrderId, txtSelectedPlace;
    private EditText editTextName;
    private Button btnEditOrder, btnOrder;

    private boolean isNewOrder = true;

    TextView textView_location, lokasiTerkiniLat, lokasiTerkiniLng;
    FusedLocationProviderClient fusedLocationProviderClient;
    private List<Pesanan> list = new ArrayList<>();
    private String orderId = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pesanan);

        txtOrderId = findViewById(R.id.txt_orderId);
        txtSelectedPlace = findViewById(R.id.txt_selectedPlace);
        editTextName = findViewById(R.id.editTxt_name);
        btnEditOrder = findViewById(R.id.btn_editOrder);
        btnOrder = findViewById(R.id.btn_order);
        textView_location = findViewById(R.id.textView_location);
        lokasiTerkiniLat = findViewById(R.id.lokasiTerkiniLat);
        lokasiTerkiniLng = findViewById(R.id.lokasiTerkiniLat);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Menjalankan permission ACCES FINE LOCATION
        if(ContextCompat.checkSelfPermission(OrderPesanan.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OrderPesanan.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        //Check Permission
        if (ActivityCompat.checkSelfPermission(OrderPesanan.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Ketika Permission Granted
            getLocation();
        } else {
            //Ketika Permission Denied
            ActivityCompat.requestPermissions(OrderPesanan.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        btnOrder.setOnClickListener(view -> { saveOrder(); });

        btnEditOrder.setOnClickListener(view -> { updateOrder(); });

        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("orderId");
            if (orderId == null) {
                txtOrderId.getText();
            } else {
                txtOrderId.setText(intent.getStringExtra("orderId"));
            }

            txtSelectedPlace.setText(intent.getStringExtra("tujuanAlamat"));
            editTextName.setText(intent.getStringExtra("name"));
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Inisialisasi Location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Inisial geoCoder
                        Geocoder geocoder = new Geocoder(OrderPesanan.this, Locale.getDefault());
                        //Inisial Address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //Set Latitude on TextView
                        lokasiTerkiniLat.setText(Html.fromHtml("<font></font>" + addresses.get(0).getLatitude()));
                        lokasiTerkiniLng.setText(Html.fromHtml("<font></font>" + addresses.get(0).getLongitude()));
                        textView_location.setText(addresses.get(0).getAddressLine(0));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        //Button Lokasi Terkini
        gMap.setMyLocationEnabled(true);

        LatLng Salatiga = new LatLng(-7.3305, 110.5084);

        selectedPlace = Salatiga;
        selectedMarker = gMap.addMarker(new MarkerOptions().position(selectedPlace));

        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace, 15.0f));

        gMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        selectedPlace = latLng;
        selectedMarker.setPosition(selectedPlace);
        gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(selectedPlace.latitude, selectedPlace.longitude, 1);
            if (addresses != null) {
                Address place = addresses.get(0);
                StringBuilder street = new StringBuilder();

                for (int i = 0; i <= place.getMaxAddressLineIndex(); i++) {
                    street.append(place.getAddressLine(i)).append("\n");
                }
                txtSelectedPlace.setText(street.toString());
            }
            else {
                Toast.makeText(this, "Could not find Address!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Error get Address!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrder() {
        Map<String, Object> order = new HashMap<>();

        String name = editTextName.getText().toString();

        order.put("name", name);
        order.put("createdDate", new Date());

        order.put("tujuanAlamat", txtSelectedPlace.getText().toString());
        order.put("tujuanLat", selectedPlace.latitude);
        order.put("tujuanLng", selectedPlace.longitude);

        //Didapatkan dari fungsi GetLocation
        order.put("terkiniAlamat", textView_location.getText().toString());
        order.put("terkiniLat", lokasiTerkiniLat.getText().toString());
        order.put("terkiniLng", lokasiTerkiniLng.getText().toString());

        String orderId = txtOrderId.getText().toString();

        if (isNewOrder) {
            db.collection("orders")
                    .add(order)
                    .addOnSuccessListener(documentReference -> {
                        editTextName.setText("");
                        txtSelectedPlace.setText("Pilih tempat");
                        txtOrderId.setText(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal tambah data order", Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            db.collection("orders").document(orderId)
                    .set(order)
                    .addOnSuccessListener(unused -> {
                        editTextName.setText("");
                        txtSelectedPlace.setText("");
                        txtOrderId.setText(orderId);

                        isNewOrder = true;
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal ubah data order", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateOrder() {
        isNewOrder = false;

        String id = txtOrderId.getText().toString();

        DocumentReference order = db.collection("orders").document(id);
        order.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.get("name").toString();

                    editTextName.setText(name);
                    txtSelectedPlace.setText(document.get("tujuanAlamat").toString());

                    LatLng resultPlace = new LatLng((double) document.get("tujuanLat"), (double) document.get("tujuanLng"));
                    selectedPlace = resultPlace;
                    selectedMarker.setPosition(selectedPlace);
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));
                }
                else {
                    isNewOrder = true;
                    Toast.makeText(this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Unable to read the DataBase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(OrderPesanan.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);

            textView_location.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


}