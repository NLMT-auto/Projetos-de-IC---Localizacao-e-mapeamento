package com.example.app_coleta_dados;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Escrita{

    private Dados dadoMemComp = new Dados();
    private MemComp shMem = new MemComp();
    private ControlArquivo arquivo = new ControlArquivo();

    private static String nomePasta = "/Dados_de_Localizacao";
    private static String nomeArquivoLeg = "Dados_coletados_FORMATADO.txt";
    private static String nomeArquivoSLeg = "Dados_coletados_SEM_FORMATACAO.txt";

    private static FileOutputStream fosExt;
    private static FileOutputStream fosExtSLeg;

    private String bufferSL[] = new String[10];
    private String bufferCL[] = new String[10];
    private int indice = 0;
    private String txtAux;

    // Verificar criação de arquivos

    private boolean chave = true; // Variável que controla o temo de vida da thread

    public void iniciaEscrita(){

        Log.i("Escrita", "Entro na thread de escrita !");

        Thread thread = new Thread(new MyRunnable() {
            @Override
            public void run() {

                chave = true; // chave de controle do tempo de vida da thread

                iniciaArquivo();

                while (chave) {
                    try {
                        buffer();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // A thread termina quando a chave passa para false
                Log.i("Escrita", "final da thread de escrita !");
            }
        });
        thread.start();
    }

    public void terminaThread ()
    {
        chave = false;
        fechaArquivo();
    }

    public void iniciaArquivo ()
    {
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/"+nomePasta+"/";
        File diretorio = new File(dir);
        if (!diretorio.exists()) {
            diretorio.mkdir();
        }

        //Quando o File() tem um parâmetro ele cria um diretório.
        //Quando tem dois ele cria um arquivo no diretório onde é informado.
        File fileExt = new File(diretorio, nomeArquivoLeg);
        File fileExtSLeg = new File(diretorio, nomeArquivoSLeg);

        //Cria os arquivos
        fileExt.getParentFile().mkdirs();
        fileExtSLeg.getParentFile().mkdirs();

        //Abre o arquivo
        fosExt = null;
        fosExtSLeg = null;

        try {
            fosExt = new FileOutputStream(fileExt, true);
            fosExtSLeg = new FileOutputStream(fileExtSLeg, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fechaArquivo ()
    {
        try {
            fosExt.close();
            fosExtSLeg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buffer () {
        while (indice < 10) {

            try{

                dadoMemComp = shMem.adiquire();
                txtAux = ((dadoMemComp.getIdent()) + " " + (dadoMemComp.getTime()) + " " + (dadoMemComp.getX()) + "  " + (dadoMemComp.getY()) + "  " + (dadoMemComp.getZ()));
                bufferSL[indice] = txtAux; // Preenchendo o vetor com os valores de String do arquivo sem legenda

                if (dadoMemComp.getIdent() == '1') {
                    txtAux = (("Acelerômetro | ") + " Timestamp: " + (dadoMemComp.getTime()) + " X: " + (dadoMemComp.getX()) + "m/s² Y: " + (dadoMemComp.getY()) + "m/s² Z: " + (dadoMemComp.getZ()) + "m/s²");
                } else if (dadoMemComp.getIdent() == '2') {
                    txtAux = (("Girômetro    | ") + " Timestamp: " + (dadoMemComp.getTime()) + " X: " + (dadoMemComp.getX()) + "m/s² Y: " + (dadoMemComp.getY()) + "m/s² Z: " + (dadoMemComp.getZ()) + "m/s²");
                } else if (dadoMemComp.getIdent() == '3') {
                    txtAux = (("GPS          | ") + " Timestamp: " + (dadoMemComp.getTime()) + " Latitude: " + (dadoMemComp.getX()) + "° Longitude: " + (dadoMemComp.getY()) + "° Altitude: " + (dadoMemComp.getZ()) + "°");
                } else {
                    Log.i("Formacao da mensagem: ", "Tem coisa errada aí");
                }
                bufferCL[indice] = txtAux; // Preenchendo o vetor com os valores de String do arquivo com legenda

                indice++; // O índice foi atualizado para os dois vetores

            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (indice == 10) {
            // Escreve nos dois arquivos
            escreveSemLegenda();
            escreveComLegenda();
            indice = 0;
        }
    }

    private void escreveComLegenda () {
        try {
            for (int i = 0; i<10; i++) {
                fosExt.write(("\n" + (bufferCL[i])).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void escreveSemLegenda () {
        try {
            for (int i = 0; i<10; i++) {
                fosExtSLeg.write(("\n" + (bufferSL[i])).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
