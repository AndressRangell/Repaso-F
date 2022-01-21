package interactor.casting_application;

import static com.flota.inicializacion.trans_init.trans.SendRcvd.TIMEOUT_DEFAULT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendRcdConfirm extends AsyncTask<Void, Integer, byte[]> {

    private static final String TAG = "SendRcdConfirm.java";

    // Messages errors
    private static final int ERROR_TIMEOUT = 1;
    private static final int ERROR_NO_ACCESS_INTERNET = 2;
    private static final int ERROR_HOST_OFF = 3;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private String ip;
    private TransPack transPack;
    private int port;
    private int timeOut;
    private Actions actions;

    private Socket clientRequest = null;
    private int resultTx = 0;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SendRcdConfirm(Context context, TransPack transPack, String ip, int port, int timeOut, Actions actions) {
        this.context = context;
        this.transPack = transPack;
        this.ip = ip;
        this.port = port;
        this.timeOut = timeOut;
        this.actions = actions;
    }

    @Override
    protected byte[] doInBackground(Void... voids) {
        long waitTime;
        byte[] rxBuf;
        byte[] lenIsoRx = new byte[2];
        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No Internet access...");
            resultTx = ERROR_HOST_OFF;
            return new byte[0];
        }
        try {
            clientRequest = new Socket();
            clientRequest.setSoTimeout(timeOut);
            clientRequest.connect(new InetSocketAddress(ip, port), 5000);
            inputStream = clientRequest.getInputStream();
            outputStream = clientRequest.getOutputStream();
            if (clientRequest.isConnected()) {
                while (true) {
                    rxBuf = transPack.packIsoInit();
                    Log.i(TAG, "Sending...");
                    Log.i(TAG, ISOUtil.hexString(rxBuf));
                    outputStream.write(rxBuf);
                    outputStream.flush();
                    waitTime = System.currentTimeMillis() + this.timeOut;
                    do {
                        if (System.currentTimeMillis() >= waitTime) {
                            resultTx = TIMEOUT_DEFAULT;
                            break;
                        }
                        int i;
                        int len;
                        long total = 0;
                        int lenpp = 0;
                        byte[] bb;
                        try {
                            byteOs = new ByteArrayOutputStream();
                            if ((i = inputStream.read(lenIsoRx)) != -1) {
                                len = ISOUtil.bcdToInt(lenIsoRx);
                                bb = new byte[len + 2];
                                lenpp = len;
                                while (len > 0 && (i = inputStream.read(bb)) != -1) {
                                    total += i;
                                    publishProgress((int) ((total * 100) / lenpp));
                                    byteOs.write(bb, 0, i);
                                    len -= i;
                                }
                                break;
                            }
                        } catch (InterruptedIOException e) {
                            Logger.exception(TAG, e);
                            return new byte[0];
                        }
                    } while (true);
                    Log.i(TAG, "Connection closing...");
                    if (resultTx == TIMEOUT_DEFAULT) {
                        break;
                    }
                    try {
                        rxBuf = byteOs.toByteArray();
                        if (rxBuf == null)
                            break;
                        //Recibe la rx del host
                        Log.i(TAG, "Receiving...");
                        Log.i(TAG, ISOUtil.hexString(rxBuf));
                        Thread.sleep(1000);
                        if (rxBuf != null) {
                            break;
                        }
                    } catch (Exception e) {
                        Logger.exception(TAG, e);
                        return new byte[0];
                    }
                }
            } else {
                Log.e("Clt", "Client no connected..");
                resultTx = ERROR_HOST_OFF;
            }
        } catch (IOException e) {
            Logger.exception(TAG, e);
            e.printStackTrace();
            Log.e(TAG, "The port of server is closed...");
            resultTx = ERROR_HOST_OFF;
            return new byte[0];
        } finally {
            try {
                if (resultTx != ERROR_HOST_OFF) {
                    inputStream.close();
                    outputStream.close();
                    clientRequest.close();
                }
            } catch (IOException e) {
                Logger.exception(TAG, e);
                e.printStackTrace();
            }
        }
        return byteOs.toByteArray();
    }

    @Override
    protected void onPostExecute(byte[] iso) {
        super.onPostExecute(iso);
        try {
            validatedMessageError(resultTx);
            actions.onShowSuccess(iso);
        } catch (Exception e) {
            Logger.exception(TAG, e);
            e.printStackTrace();
        }
    }

    public void validatedMessageError(int msgError) {
        switch (msgError) {
            case ERROR_TIMEOUT:
                actions.onShowError("ERROR, TIEMPO DE ESPERA AGOTADO");
                break;
            case ERROR_NO_ACCESS_INTERNET:
                actions.onShowError("ERROR, NO HAY CONEXIÓN A INTERNET");
                break;
            case ERROR_HOST_OFF:
                actions.onShowError("ERROR, NO HAY CONEXIÓN CON EL SERVIDOR");
                break;
            default:
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public interface Actions {
        void onShowError(String msg);

        void onShowSuccess(byte[] rsp);
    }
}
