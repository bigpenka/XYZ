package com.example.xyz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class listaPrincipal  extends AppCompatActivity {
static  String MQTTHOST = "tcp://crudfirebasev1.cloud.shiftr.io:1883";
    static  String MQTTUSER ="crudfirebasev1";
    static  String MQTTPASS="zSr6Tqt1UO2LbyrT";
    MqttAndroidClient cliente;
    String clienteID="";
    Boolean permisoPublicar;


    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_principal);
        connectBroker();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MainModel> options = new FirebaseRecyclerOptions.Builder<MainModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Inventario Empresa"),MainModel.class)
                .build();
        mainAdapter = new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Agregar.class));
            }
        });
    }
    private void checkConnection(){
        if (this.cliente.isConnected()){
            this.permisoPublicar=true;
        }else{
            this.permisoPublicar=false;
            connectBroker();
        }
    }

    private void connectBroker() {
        this.cliente= new MqttAndroidClient(this.getApplicationContext(),MQTTHOST, this.clienteID);
        
    }

    @Override
    protected  void onStart(){
        super.onStart();
        mainAdapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        mainAdapter.startListening();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.resource, menu);
        MenuItem item = menu.findItem(R.id.buscar);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void txtSearch(String str){
        FirebaseRecyclerOptions<MainModel> options = new FirebaseRecyclerOptions.Builder<MainModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Inventario Empresa").orderByChild("Nombre")
                        .startAt(str).endAt(str+"~"),MainModel.class)
                .build();

        mainAdapter = new MainAdapter(options);
        mainAdapter.startListening();
        recyclerView.setAdapter(mainAdapter);
    }
}

