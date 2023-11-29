package com.example.app_coleta_dados;

import android.location.Location;

import java.sql.Timestamp;
import java.util.Calendar;

public class Dados {

    public String time;
    public char identificador; // 1->Acelerômetro 2->Girômetro 3->GPS
    private double X = 0;
    private double Y = 0;
    private double Z = 0;

    public void setDado (double dadoX, double dadoY, double dadoZ, String tempo, char identificador) {
        this.time = tempo;
        this.identificador = identificador;
        this.X = dadoX;
        this.Y = dadoY;
        this.Z = dadoZ;
    }

    public char getIdent () {
        return identificador;
    }

    public String getTime () {
        return time;
    }

    public double getX () {
        return X;
    }

    public double getY () {
        return Y;
    }

    public double getZ () {
        return Z;
    }
}