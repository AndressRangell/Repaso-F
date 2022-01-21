package com.newpos.libpay;

import android.os.AsyncTask;
import android.os.Environment;

import com.flota.inicializacion.configuracioncomercio.APLICACIONES;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.flota.tools.Log;
import com.newpos.libpay.utils.PAYUtils;
import com.wposs.flota.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.desert.newpos.payui.UIUtils;

import static com.flota.defines_bancard.DefinesBANCARD.POLARIS_APP_NAME;
import static com.newpos.libpay.LogType.COMUNICACION;
import static com.newpos.libpay.LogType.CONSOLE;
import static com.newpos.libpay.LogType.ERROR;
import static com.newpos.libpay.LogType.EXCEPTION;
import static com.newpos.libpay.LogType.FLUJO;
import static com.newpos.libpay.LogType.PRINT;
import static com.newpos.libpay.LogType.REVERSAL;
import static com.newpos.libpay.LogType.TIMER;

/**
 * Created by zhouqiang on 2017/3/8.
 *
 * @author zhouqiang
 * sdk全局日主输出
 */

public class Logger extends AsyncTask<Void, String, Void> {


    public static final String ACTUAL_CLASS = "Logger.java";
    private static final String ERROR_MAX_PESO = "Error: El archivo de logs no se puede seguir modificando, maximo tamaño excedido";
    private static final String FILE_SEPARATOR = "/";
    public static final String TEMP_FOLDER = "TempLogs";
    private static final String VERSION = Tools.getVersion();

    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    LogType logType;
    String mensaje;
    static boolean isDebug = BuildConfig.DEBUG;

    private final File rutaSd = Environment.getExternalStorageDirectory();
    private int opc = 0;

    private Logger(LogType logType, String mensaje) {
        this.logType = logType;
        this.mensaje = mensaje;
    }

    private static void getInstanceClassLogger(LogType logType, String msg) {
        Logger classLogger = new Logger(logType, msg);
        classLogger.execute();
    }

    /**
     * @param info Array of Strings with info to log
     *             debug("String1","String2","String3",...)
     *             Debug Logs
     */
    public static void debug(String... info) {
        if (isDebug) {
            consoleLog(CONSOLE, getMessage(info, null));
        }
    }

    /**
     * @param info Array of Strings with info to log
     *             error("String1","String2","String3",...)
     *             Error Logs
     */
    public static void error(String... info) {
        if (isDebug) {
            consoleLog(ERROR, getMessage(info, null));
        }
    }

    /**
     * @param msg String with info to log
     * @param e   Exception to log
     *            Error Log
     */
    public static void error(String msg, Throwable e) {
        if (isDebug) {
            consoleLog(ERROR, getMessage(new String[]{msg}, e));
        }
    }

    /**
     * @param info Array of Strings with info to log
     *             reversal("String1","String2","String3",...)
     *             Reversal Log
     */
    public static void reversal(String... info) {
        String message = getMessage(info, null);
        getInstanceClassLogger(REVERSAL, message);
        if (isDebug) {
            consoleLog(REVERSAL, getMessage(info, null));
        }
    }

    /**
     * @param msg String with info to log
     * @param e   Exception to log
     *            Reversal Log
     */
    public static void reversal(String msg, Throwable e, String metodo) {
        String message = getMessage(new String[]{metodo, msg}, e);
        getInstanceClassLogger(REVERSAL, message);
        if (isDebug) {
            consoleLog(REVERSAL, message);
        }
    }

    /**
     * @param info Array of Strings with info to log
     *             info("String1","String2","String3",...)
     *             Info Log
     */
    public static void info(String... info) {
        if (isDebug) {
            consoleLog(COMUNICACION, getMessage(info, null));
        }
    }

    /**
     * @param info Array of Strings with info to log
     *             exception("String1","String2","String3",...)
     *             Exception Log
     */
    public static void exception(String... info) {
        String message = getMessage(info, null);
        getInstanceClassLogger(EXCEPTION, message);
        if (isDebug) {
            consoleLog(EXCEPTION, getMessage(info, null));
        }
    }

    /**
     * @param msg String with info to log
     * @param e   Exception to log
     *            Exception Log
     */
    public static void exception(String msg, Throwable e) {
        String message = getMessage(new String[]{msg}, e);
        getInstanceClassLogger(EXCEPTION, message);
        if (isDebug) {
            consoleLog(EXCEPTION, message);
        }
    }

    /**
     * @param info Array of Strings with info to log
     *             comunicacion("String1","String2","String3",...)
     *             Comunicaion Log
     */
    public static void comunicacion(String... info) {
        String message = getMessage(info, null);
        getInstanceClassLogger(COMUNICACION, message);
        info(info);
    }

    /**
     * @param info Array of Strings with info to log
     *             comunicacion("String1","String2","String3",...)
     *             Comunicaion Log
     */
    public static void flujo(String... info) {
        String message = getMessage(info, null);
        getInstanceClassLogger(FLUJO, message);
        info(info);
    }

    /**
     * @param info Array of Strings with info to log
     *             timer("String1","String2","String3",...)
     *             Timer Log
     */
    public static void timer(String... info) {
        String message = getMessage(info, null);
        getInstanceClassLogger(TIMER, message);
        if (isDebug) {
            consoleLog(TIMER, getMessage(info, null));
        }
    }

    /**
     * @param info Array of Strings with info to log
     *             print("String1","String2","String3",...)
     *             Print Log
     */
    public static void print(String... info) {
        String message = getMessage(info, null);
        getInstanceClassLogger(PRINT, message);
        if (isDebug) {
            consoleLog(PRINT, getMessage(info, null));
        }
    }


    /**
     * @param info Array of Strings with info to log
     * @param e    Exception to log
     * @return Message created by info concat with throwable information
     */
    private static String getMessage(String[] info, Throwable e) {
        StringBuilder msg = new StringBuilder();
        for (String s : info) {
            if (msg.indexOf(s) == -1) {
                msg.append(s).append(" - ");
            }
        }
        if (e != null) {
            msg.append("\n").append(getStackLog(e.getStackTrace()));
        }
        return msg.toString();
    }

    /**
     * @param e StackTraceElement[] with exception information
     * @return exception information in String
     */
    private static String getStackLog(StackTraceElement[] e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e) {
            sb.append("Nom del archivo: ").append(element.getFileName()).append("\n");
            sb.append("Num de la linea: ").append(element.getLineNumber()).append("\n");
            sb.append("Nomb del metodo: ").append(element.getMethodName()).append("\n");
            sb.append("--" + "\n");
        }
        return sb.toString();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            APLICACIONES aplicaciones = APLICACIONES.getSingletonInstanceAppActual(POLARIS_APP_NAME);
            if (aplicaciones != null) {
                boolean isLog = false;
                switch (logType) {
                    case ERROR:
                    case FLUJO:
                        isLog = aplicaciones.isLogFlujo();
                        break;
                    case EXCEPTION:
                        isLog = aplicaciones.isLogExcepciones();
                        break;
                    case COMUNICACION:
                        isLog = aplicaciones.isLogComunicacion();
                        break;
                    case TIMER:
                        isLog = aplicaciones.isLogTimer();
                        break;
                    case PRINT:
                        isLog = aplicaciones.isLogImpresion();
                        break;
                    default:
                        break;
                }
                String message = logType.name() + ": " + mensaje;
                reversalLog(message);
                if (isLog) normalLog(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param message String with info to log
     *                Logs for case Reversal + Pint
     */
    private void reversalLog(String message) {
        String date = PAYUtils.getLocalDate();
        File folder = createFolder(rutaSd.getAbsolutePath() + "/" + TEMP_FOLDER + "/");
        String fileName = "rev-credeb-" + VERSION + "-" + date + ".txt";
        writeLog(folder, fileName, message);
    }

    /**
     * @param message String with info to log
     *                Normal Logs
     */
    private void normalLog(String message) {
        File folder = createFolder(rutaSd.getAbsolutePath() + "/LogsWposs/" + POLARIS_APP_NAME + "/" + VERSION);
        String date = PAYUtils.getLocalDate();
        String fileName = "logsdata" + date + ".txt";
        writeLog(folder, fileName, message);
    }

    /**
     * @param type LogType that indicates the type of log
     * @param msg  String with info to log
     *             Generate logs in Android Studio console
     */
    private static void consoleLog(LogType type, String msg) {
        String name = type.name();
        switch (type) {
            case FLUJO:
            case COMUNICACION:
                Log.v(name, msg);
                break;
            case EXCEPTION:
            case ERROR:
                Log.e(name, msg);
                break;
            case CONSOLE:
                Log.d(name, msg);
                break;
            default:
                Log.i(name, msg);
                break;
        }
    }

    /**
     * @param folder   Path to save logs file
     * @param fileName name of file to write
     * @param message  String with info to log
     *                 Generate a File in specific path and write logs message
     */
    private void writeLog(File folder, String fileName, String message) {
        File logFile = new File(folder.getAbsoluteFile() + FILE_SEPARATOR + fileName);
        String time = PAYUtils.getLocalTime();
        checkSize(logFile);
        if (logFile.exists() && opc == 1) {
            consoleLog(ERROR, ERROR_MAX_PESO);
            return;
        }
        try {
            fileWriter = new FileWriter(logFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            if (logFile.exists() && opc == 0) {
                FileReader fileReader = new FileReader(logFile);
                try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                    StringBuilder file = new StringBuilder();
                    for (String leer; (leer = bufferedReader.readLine()) != null; ) {
                        file.append(leer);
                        file.append("\n");
                    }
                    bufferedWriter.write(file.toString());
                }
            } else {
                bufferedWriter.newLine();

            }
            bufferedWriter.append(time).append(" - ").append(message);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            String error = getStackLog(e.getStackTrace());
            if (e instanceof FileNotFoundException) {
                error = "FileNotFoundException " + getStackLog(e.getStackTrace());
            }
            consoleLog(EXCEPTION, error);
        }
    }


    /**
     * @param dirPath Path where the folder will be created
     * @return File of the created folder
     */
    private File createFolder(String dirPath) {
        File carpeta = new File(dirPath);
        if (carpeta.mkdirs()) {
            consoleLog(ERROR, "Error: Folder wasn't created");
        }
        if (!carpeta.exists()) {
            consoleLog(ERROR, "Error: File don't exist");
        }
        return carpeta;
    }

    /**
     * @param file item to check
     *             Check the actual size of a file, with maximum size allowed
     */
    private void checkSize(File file) {
        float length = file.length();
        if (length > 5120000) {
            opc = 1;
        } else {
            opc = 0;
        }
    }

    /**
     * Copy Reversal Log file to default path
     */
    public static void copyLogsFile() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            String date = PAYUtils.getLocalDate();

            String fileName = "rev-credeb-" + VERSION + "-" + date + ".txt";
            String sourcePath = sd.getAbsolutePath() + FILE_SEPARATOR + TEMP_FOLDER + FILE_SEPARATOR;
            String destinationPath = sd.getAbsolutePath() + "/LogsWposs/";
            File source = new File(sourcePath + FILE_SEPARATOR + fileName);
            File destination = new File(destinationPath + FILE_SEPARATOR + fileName);
            boolean res = source.renameTo(destination);
            consoleLog(COMUNICACION, "Move File " + source.getAbsolutePath() + " To " + destination + " " + res);
        } catch (Exception e) {
            consoleLog(EXCEPTION, getStackLog(e.getStackTrace()));
        }
    }

    /**
     * Delete Reversal Logs Folder
     */
    public static void deleteLogs() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            String sourcePath = sd.getAbsolutePath() + FILE_SEPARATOR + TEMP_FOLDER + FILE_SEPARATOR;
            File source = new File(sourcePath);
            UIUtils.delete(source);
        } catch (Exception e) {
            consoleLog(EXCEPTION, getStackLog(e.getStackTrace()));
        }
    }

    /**
     * @return Actual cant of Files in Temp Log Folder
     */
    public static int getFilesCant() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            String sourcePath = sd.getAbsolutePath() + FILE_SEPARATOR + TEMP_FOLDER + FILE_SEPARATOR;
            File source = new File(sourcePath);
            int cant = source.listFiles().length;
            consoleLog(CONSOLE, "getFilesCant: cant: " + cant);
            return cant;
        } catch (Exception e) {
            consoleLog(EXCEPTION, getStackLog(e.getStackTrace()));
        }
        return 0;
    }
}
