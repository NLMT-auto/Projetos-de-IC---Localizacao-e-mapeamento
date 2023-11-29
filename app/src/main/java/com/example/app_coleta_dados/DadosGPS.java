package com.example.app_coleta_dados;

import android.location.Location;

import java.util.Calendar;

public class DadosGPS extends Dados {

    @Override
    public void setDado(double dadoX, double dadoY, double dadoZ, String timestamp, char identificador) {
        super.setDado(dadoX, dadoY, dadoZ, timestamp, identificador);
    }
}

