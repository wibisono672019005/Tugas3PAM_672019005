package com.example.pam_googlemapsfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pam_googlemapsfirebase.adapter.PesananAdapter;
import com.example.pam_googlemapsfirebase.model.Pesanan;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Variabel
    private RecyclerView recyclerView;
    private FloatingActionButton btnAddOrder;

    private FirebaseFirestore db;

    TextView textView_location;
    FusedLocationProviderClient fusedLocationProviderClient;
    private List<Pesanan> list = new ArrayList<>();
    private PesananAdapter pesananAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_location = findViewById(R.id.textView_location);

        recyclerView = findViewById(R.id.recyclerView);
        btnAddOrder = findViewById(R.id.btn_addOrder);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mengambil Data...");
        pesananAdapter = new PesananAdapter(getApplicationContext(), list);
        pesananAdapter.setDialog(new PesananAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                final CharSequence[] dialogItem =
                        {"Pesanan " + list.get(pos).getName().toString(), "Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                break;
                            case 1:
                                Intent intent = new Intent(getApplicationContext(), EditPesanan.class);
                                intent.putExtra("orderId", list.get(pos).getOrderId());
                                intent.putExtra("tujuanAlamat", list.get(pos).getTujuan());
                                intent.putExtra("name", list.get(pos).getName());
                                startActivity(intent);
                                break;
                            case 2:
                                deleteData(list.get(pos).getOrderId());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

        //Pembuatan List Riwayat Pemesanan
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(pesananAdapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        db = FirebaseFirestore.getInstance();

        //Runtime Permissions for get access for location
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }


        btnAddOrder.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), OrderPesanan.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        progressDialog.show();
        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Pesanan pesanan = new Pesanan(
                                        document.getString("orderId"),
                                        document.getString("tujuanAlamat"),
                                        document.getString("terkiniAlamat"),
                                        document.getString("name")
                                );
                                pesanan.setOrderId(document.getId());
                                list.add(pesanan);
                            }
                            pesananAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Data Gagal di Ambil dari Database!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void deleteData(String orderId) {
        progressDialog.show();
        db.collection("orders").document(orderId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Data Gagal di Hapus!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();
                    }
                });
    }
}