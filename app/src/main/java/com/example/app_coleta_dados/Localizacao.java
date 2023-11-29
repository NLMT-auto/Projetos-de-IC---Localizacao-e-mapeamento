package com.example.app_coleta_dados;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class Localizacao extends AppCompatActivity{

    private final String TAG = "MainActivity";

    private Context context;

    private String txt;

    private String string;

    private boolean chave = true; // Chave da thread

    private DadosGPS dado = new DadosGPS();
    private MemComp shMem = new MemComp();

    private long temp;
    private String tempo;

    private CancellationTokenSource cancellationSource = new CancellationTokenSource();

    public void iniciaLocalizacao(Context context){

        Log.i("Localização", "Entro na thread de localização !");

        this.context = context;

        Thread thread = new Thread(new MyRunnable() {
            @Override
            public void run() {

                chave = true; // chave de controle do tempo de vida da thread

                while (chave) {
                    try {
                        getLocation();
                        Thread.sleep(500);//
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // A thread termina quando a chave passa para false

                Log.i("Localização", "final da thread de localização !");
            }
        });
        thread.start();
    }

    public void terminaThread ()
    {
        chave = false;
    }

    private void getLocation()
    {
        // The Fused Location Provider provides access to location APIs.
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        requestCurrentLocation(fusedLocationClient, context);
    }

    private void requestCurrentLocation(FusedLocationProviderClient fusedLocationClient, Context context) {
        // Request permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // Main code
            @SuppressLint("MissingPermission") Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(100, cancellationSource.getToken());

            currentLocationTask.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    String latitude = "";
                    String longitude = "";
                    String altitude = "";

                    if (task.isSuccessful()) {

                        Location location = task.getResult();
                        temp = converteTime();
                        tempo = "" + temp;
                        dado.setDado(location.getLatitude(), location.getLongitude(), location.getAltitude(), tempo, '3');

                        string = ("Localizacao" + location.getLatitude() + ": " + location.getLongitude() + ": " + location.getAltitude());
                        Log.i("Localizacao", location.getLatitude() + ": " + location.getLongitude() + ": " + location.getAltitude());
                        //Toast.makeText(context, location.getLatitude() + ": " + location.getLongitude() + ": " + location.getAltitude(), Toast.LENGTH_SHORT).show();

                        //muda a view na tela
                        txt = (location.getLatitude() + ": " + location.getLongitude() + ": " + location.getAltitude());

                        try {
                            shMem.Escreve(dado);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                    }
                }
            }));
        } else {
            // TODO: Request fine location permission
            Log.d(TAG, "Request fine location permission.");
        }
    }

    private long converteTime()
    {
        long time = 0;
        Calendar calendar = Calendar.getInstance();
        //calendar.set(ano, mes, dia);
        calendar.getTimeInMillis();

        time = calendar.getTimeInMillis();
        return time;
    }
}
