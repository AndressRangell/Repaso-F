package interactor.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.newpos.libpay.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateImpl extends DateUtil {

    public static final String TAG = "DateImpl.java";

    Context context;

    public DateImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean detectNewDay(String dateInitial, String day, String hour, String time) {
        if ((dateInitial == null || dateInitial.isEmpty()) ||
                day == null || day.isEmpty() || hour == null || hour.isEmpty()) {
            return false;
        }

        try {
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(new Date());

            Date dateIni = dateFormat.parse(dateInitial);
            Date dateFin = dateFormat.parse(date);
            int rta = (int) ((dateFin.getTime() - dateIni.getTime()) / 86400000);

            if (rta > 0) {
                int diasProximoCierre = Integer.parseInt(day);
                Log.e("CIERRE", "Dias que deben pasar para hacer el cierre.  = " + diasProximoCierre);
                if (rta >= diasProximoCierre) {
                    return Integer.parseInt(time) >= Integer.parseInt(hour);
                }
            }
        } catch (Exception e) {
            Logger.exception(TAG, e);
        }
        return false;
    }
}
