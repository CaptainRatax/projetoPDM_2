package com.example.projetopdmsam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmsam.Backend.BaseDados;
import com.example.projetopdmsam.Backend.RetrofitClient;
import com.example.projetopdmsam.Modelos.Inspecao;
import com.example.projetopdmsam.Modelos.Obra;
import com.example.projetopdmsam.Modelos.Utilizador;
import com.example.projetopdmsam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginaInicial extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    private static final int PERMISSION_REQUEST_CODE = 200;

    Utilizador loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loggedInUser = bd.getLoggedInUser();

        Inspecao inspecaoADecorrer = bd.getInspecaoADecorrer();

        if(inspecaoADecorrer.isActive()){
            Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
            startActivity(intent);
        }else{
            if(isInternetAvailable()){
                Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getInspecaoAtivaPorIdInspetor(loggedInUser.getId());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.body().get("Success").getAsBoolean()){
                            JsonObject obraJson = response.body().get("Obra").getAsJsonObject();
                            //Cria um objeto do tipo Obra usando o Json que recebeu da API
                            Obra obra = new Obra();
                            obra.setId(obraJson.get("Id").getAsInt());
                            obra.setNome(obraJson.get("Nome").getAsString());
                            obra.setDescricao(obraJson.get("Descricao").getAsString());
                            obra.setCodigoPostal(obraJson.get("CodigoPostal").getAsString());
                            obra.setLocalidade(obraJson.get("Localidade").getAsString());
                            obra.setPais(obraJson.get("Pais").getAsString());
                            obra.setDataInicio(obraJson.get("DataInicio").getAsString());
                            obra.setResponsavel(obraJson.get("Responsavel").getAsString());
                            obra.setActive(obraJson.get("IsActive").getAsBoolean());

                            JsonObject inspecao = response.body().get("Inspecao").getAsJsonObject();
                            //Cria um objeto do tipo Inspecao usando o Json que recebeu da API
                            inspecaoADecorrer.setId(inspecao.get("Id").getAsInt());
                            inspecaoADecorrer.setDataInicio(inspecao.get("DataInicio").getAsString());
                            inspecaoADecorrer.setDataFim(inspecao.get("DataFim").getAsString());
                            inspecaoADecorrer.setFinished(inspecao.get("IsFinished").getAsBoolean());
                            inspecaoADecorrer.setInspetorId(inspecao.get("InspectorId").getAsInt());
                            inspecaoADecorrer.setObraId(inspecao.get("ObraId").getAsInt());
                            inspecaoADecorrer.setActive(inspecao.get("IsActive").getAsBoolean());
                            if (bd.getInspecaoADecorrer().isActive()) { //Verifica se existe alguma inspeção a decorrer localmente
                                //Existe uma inspeção a decorrer localmente
                                if (bd.getInspecaoADecorrer() != inspecaoADecorrer) { //Verifica se a inspeção que está a decorrer localmente é diferente da recebida
                                    bd.acabarInspecaoLocal(); //Se for acaba a inspeção local
                                    bd.comecarInspecaoLocal(inspecaoADecorrer); //e começa uma nova com os dados da inspeção recebida
                                }
                            } else {
                                //Não existe nenhuma inspeção a decorrer localmente
                                bd.comecarInspecaoLocal(inspecaoADecorrer); //Começa a inspeção localmente
                            }
                            if (bd.getObraPorId(obra.getId()).isActive()) {//Verifica se a obra já existe localmente
                                //A obra existe localmente
                                if (bd.getObraPorId(obra.getId()) != obra) {//Verifica se a obra que existe localmente é diferente da recebida
                                    bd.editarObra(obra); //Se for altera a obra local e coloca os dados da obra recebida
                                }
                            } else {
                                //A obra não existe localmente
                                bd.adicionarObra(obra); //Cria a obra localmente
                            }
                            Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_inicial);

        TextView txt_BemVindo = findViewById(R.id.txt_BemVindo);
        txt_BemVindo.setText("Bem vindo " + loggedInUser.getNome() + "!");

        FloatingActionButton btn_Logout = findViewById(R.id.btn_Logout);
        Button btn_QRCodeScanner = findViewById(R.id.btn_QRCodeScanner);

        btn_QRCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()){
                    if(isInternetAvailable()){
                        Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(PaginaInicial.this, "É necessário uma conexão à internet...", Toast.LENGTH_LONG).show();
                    }
                }else{
                    requestPermission();
                    Toast.makeText(PaginaInicial.this, "É necessário aceitar a permissão de acesso à câmara!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bd.logoutLocal();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

    }

    //Funções auxiliares

    private boolean isInternetAvailable(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PaginaInicial.this)
                .setMessage(message)
                .setPositiveButton("Aceitar", okListener)
                .setNegativeButton("Negar", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                    startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("É necessário aceitar a permissão de acesso à câmara!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

}