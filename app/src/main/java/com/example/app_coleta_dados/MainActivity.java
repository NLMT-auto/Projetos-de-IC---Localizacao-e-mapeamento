package com.example.app_coleta_dados;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager = null;

    // angular speeds from gyro
    private float[] gyro = new float[3];

    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];

    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];

    // accelerometer vector
    private float[] accel = new float[3];

    private double timestamp;
    private String tempo;

    Calendar calendar = Calendar.getInstance();
    //calendar.set(ano, mes, dia);

    private DadosGyro dadoGyro = new DadosGyro();
    private DadosAcel dadoAcel = new DadosAcel();
    //Variável para o envio das strings para memória compartilhada
    private MemComp shMem = new MemComp();

    // variáveis de status dos sensores.
    TextView acelerometroTxt;
    TextView girometroTxt;

    // Variáveis de controle de visibilidade do progresso
    TextView textoDeLoading;
    TextView caminhoDaPasta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialização das variáveis de status dos sensores.
        acelerometroTxt = (TextView) findViewById(R.id.chec_acel);
        girometroTxt = (TextView) findViewById(R.id.chec_gyro);

        //Inicialização da variável de texto do progresso
        textoDeLoading = (TextView) findViewById(R.id.texto_barra_de_progresso);
        caminhoDaPasta = (TextView) findViewById(R.id.caminhoDaPasta);

        // Coloca a visibilidade da barra do progresso em invisível
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);


        //Permissão de acesso ao GPS Localização fina
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        //Permissão de acesso à memória interna
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        //Permissão de acesso ao GPS Localização grosseira
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        //Permissão de acesso à Câmera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
        }
        //Permissão de acesso à Câmera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
        }

        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;

        // get sensorManager and initialise sensor listeners
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        //Conferindo se os sensores existem no dispositivo
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // sucesso, temos um acelerometro!
            Log.i("Script", "Temos um acelerômetro");
            acelerometroTxt.setText( "Este dispositivo tem acelerômetro ? Sim");
        } else {
            // falha, não temos um acelerometro!
            Log.i("Script", "NAO temos um acelerômetro");
            acelerometroTxt.setText("Este dispositivo tem acelerômetro ? Não");
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // sucesso, temos um giroscópio!
            Log.i("Script", "Temos um giroscopio");
            girometroTxt.setText("Este dispositivo tem girômetro ? Sim");
        } else {
            // falha, não temos um giroscópio!
            Log.i("Script", "NAO temos um giroscopio");
            girometroTxt.setText("Este dispositivo tem girômetro ? Não");
        }

        initListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int[] grantResults)
    {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permissão nao concedida!", Toast.LENGTH_SHORT).show();
                    finish();
                }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister sensor listeners to prevent the activity from draining the device's battery.
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister sensor listeners to prevent the activity from draining the device's battery.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore the sensor listeners when user resumes the application.
        initListeners();
    }

    public void initListeners() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
  }

    public void onSensorChanged(SensorEvent event) {
        /*switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array
                // then calculate new orientation
                System.arraycopy(event.values, 0, accel, 0, 3);
                tempo = converteTime();
                dadoAcel.setDado(accel[1], accel[2], accel[0], tempo, '1');

                //Manda o objeto do acelerômetro
                try {
                    shMem.Escreve(dadoAcel); // Aqui o objeto atualizado é enviado para a memória compartilhada
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                // process gyro data
                gyroFunction(event);
                break;
        }*/

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void gyroFunction(SensorEvent event) {
        System.arraycopy(event.values, 0, gyro, 0, 3);
        tempo = converteTime();
        dadoGyro.setDado(gyro[0], gyro[1], gyro[2], tempo, '2');

        //Manda o objeto do acelerômetro
        try {
            shMem.Escreve(dadoGyro);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String converteTime()
    {
        String time = "";
        Calendar calendar = Calendar.getInstance();
        //calendar.set(ano, mes, dia);
        calendar.getTimeInMillis();

        time = ("" + calendar.getTimeInMillis());
        return time;
    }

    ////////////////////////////// ACIONAMENTO DO BOTÃO PARA A LOCALIZACAO /////////////////////////////////////

    public void startService(View view)
    {
        Intent it = new Intent(this, Servico.class);
        startService(it);
        textoDeLoading.setText("Coleta de dados em progresso");
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        Intent intent = new Intent(MainActivity.this, Camera.class);
        MainActivity.this.startActivity(intent);
        //tiraFoto();
    }

    public void stopService(View view)
    {
        Intent it = new Intent(this, Servico.class);
        stopService(it);
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        textoDeLoading.setText("Coleta de dados finalizada !");
        caminhoDaPasta.setText("Os documentos foram salvos no armazenamento \ninterno -> pasta downloads -> Dados_de_Localizacao\n\n/storage/emulated/0/Download/Dados_de_Localizacao/");
    }

}