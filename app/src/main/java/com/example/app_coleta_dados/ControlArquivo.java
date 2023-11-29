package com.example.app_coleta_dados;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ControlArquivo {

    private static String nomePasta = "/Dados_de_Localizacao";
    private static String nomeArquivoLeg = "Dados_coletados_FORMATADO.txt";
    private static String nomeArquivoSLeg = "Dados_coletados_SEM_FORMATACAO.txt";

    private static FileOutputStream fosExt;
    private static FileOutputStream fosExtSLeg;

    // Verificar criação de arquivos

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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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

    public void escreveComLegenda () {}

    public void escreveSemLegenda () {}

    /*private String converteTime()
    {
        String time = "";
        Calendar calendar = Calendar.getInstance();
        //calendar.set(ano, mes, dia);
        calendar.getTimeInMillis();

        time = ("" + calendar.getTimeInMillis());
        return time;
    }*/
}
