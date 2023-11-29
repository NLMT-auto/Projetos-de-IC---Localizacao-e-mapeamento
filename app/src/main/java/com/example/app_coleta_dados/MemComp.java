package com.example.app_coleta_dados;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Semaphore;

public class MemComp extends AppCompatActivity {

    private static Dados dados = new Dados();

    //private boolean semaforo = false; // false == travado / true == destravado
    private Semaphore semaforo = new Semaphore(6); // Número de threads do app

    // usar a variável própria de semaforo do Java semaforo ou mutex

    public Dados adiquire () throws InterruptedException {
        // travar a memória, adquire os dados, destravar a memória
        Dados pkg = new Dados();

        semaforo.acquire();// trava a memória
        pkg = dados;
        semaforo.release();// destrava a memória

        return pkg;
    }

    public void Escreve (Dados obj) throws InterruptedException {
        // travar a memória, escreve os dados, destravar a memória

        semaforo.acquire();// trava a memória
        dados = obj; //escreve os dados do a serem escritos no objeto principal
        semaforo.release();// destrava a memória
    }
}
