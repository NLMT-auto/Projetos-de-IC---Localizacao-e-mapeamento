package com.example.app_coleta_dados;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Service;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Servico extends Service {

    //Variável de localização
    Localizacao local = new Localizacao();

    //Variável de GeraArquivo
    MemComp shMem = new MemComp();

    //Variável de Camera
    Camera camera = new Camera();

    //Main - teste para implementação da camera
    MainActivity main = new MainActivity();

    //Variável da classe de escrita
    Escrita escrita = new Escrita();

    //Variável de controle de número de vezes que o botão inciar é criado.
    private int controlStart = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void verificaStart ()
    {
        if (controlStart == 0){
            // Escrita
            escrita.iniciaEscrita(); // Inicia a thread da escrita dos arquivos
            // Localização
            local.iniciaLocalizacao(this); // Inicia a thread de controle de GPS
            // Camera
            //camera.iniciaCamera(this);
        }else{
            Toast.makeText(this, "Threads já criadas em funcionamento!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("Script", "OnstartCommand()");
        verificaStart();
        controlStart++;
        return(super.onStartCommand(intent,flags,startId));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Lógica de finalização de threads
        controlStart--;
        local.terminaThread();
        escrita.terminaThread();
        //camera.terminaThread();
    }


    // criar 3 threads de leitura e processamento de dados
}
