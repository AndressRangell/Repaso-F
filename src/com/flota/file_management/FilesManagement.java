package com.flota.file_management;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import com.newpos.libpay.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.flota.defines_bancard.DefinesBANCARD.NAME_FOLDER_CTL_CAPKS;
import static com.flota.defines_bancard.DefinesBANCARD.NAME_FOLDER_CTL_FILES;

public class FilesManagement {

    private String clase = "Files_Management.java";
    private Context mContext;
    private String mfileData;

    public FilesManagement(Context mContext) {
        this.mContext = mContext;
        this.mfileData = "";
    }

    public String getMfileData() {
        return mfileData;
    }

    public boolean readFile(String fileName) {
        boolean ret = false;
        String line = null;
        StringBuilder stringBuffer = new StringBuilder("");
        File ruta_sd = Environment.getExternalStorageDirectory();
        File f = new File(ruta_sd.getAbsolutePath(), fileName);
        try (BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(f)));) {
            while ((line = fin.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            mfileData = stringBuffer.toString();
            ret = true;
        } catch (Exception ex) {
            Logger.exception(clase, ex);
            Logger.error("Ficheros", "Error al leer archivo");
        }

        return ret;
    }

    /**
     * Realiza la lectura de los archivo de configuracion del CTL
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static byte[] readFileBin(String fileName, Context context) {

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(NAME_FOLDER_CTL_FILES, Context.MODE_PRIVATE);
        File f = new File(directory, fileName);

        try {
            InputStream ios = null;
            ios = new FileInputStream(f);
            byte[] data = new byte[1 << 20];
            int length = 0;
            try {
                length = ios.read(data);
                ios.close();
                if (length != -1){
                    byte[] out = new byte[length];
                    System.arraycopy(data, 0, out, 0, length);
                    return out;
                }else {
                    return null;
                }
            } catch (IOException e) {
                Logger.exception("Files_Management.java", e);
                e.printStackTrace();
                return null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Realiza la lectura de los archivo de CAKEY para CTL
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static byte[] readFileBinCakey(String fileName, Context context) {

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(NAME_FOLDER_CTL_CAPKS, Context.MODE_PRIVATE);
        File f = new File(directory, fileName);

        try {
            InputStream ios = null;
            ios = new FileInputStream(f);
            byte[] data = new byte[1 << 20];
            int length = 0;
            try {
                length = ios.read(data);
                ios.close();
                byte[] out = new byte[length];
                System.arraycopy(data, 0, out, 0, length);
                return out;
            } catch (IOException e) {
                Logger.exception("Files_Management.java", e);
                e.printStackTrace();
                return null;
            }

        } catch (FileNotFoundException e) {
            Logger.exception("Files_Management.java", e);
            e.printStackTrace();
            return null;
        }
    }
}
