package com.flota.actividades;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flota.inicializacion.trans_init.Init;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.io.File;

import cn.desert.newpos.payui.master.FormularioActivity;

import static com.flota.inicializacion.trans_init.Init.DEFAULT_DOWNLOAD_PATH;

public class FalloConexionActivity extends FormularioActivity {
    String clase = "FalloConexionActivity.java";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_mensaje_confirmacion);
        TextView tvTitulo = findViewById(R.id.tvTitulo);
        tvTitulo.setVisibility(View.INVISIBLE);
        TextView mensaje = findViewById(R.id.mensaje);
        mensaje.setText("Inicialización fallida. Comuníquese con soporte o intente nuevamente.");
        Button btnSi = findViewById(R.id.btnSi);
        Button btnNo = findViewById(R.id.btnNo);
        FloatingActionButton btn_reinicializar = findViewById(R.id.btn_reinicializar);
        btn_reinicializar.setVisibility(View.VISIBLE);
        mostrarSerialvsVersion();
        btn_reinicializar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarCache(getApplicationContext(), clase);
                Logger.comunicacion( clase, "Eliminando Directorio: " + new File(DEFAULT_DOWNLOAD_PATH).getAbsolutePath());
                eliminarDatos(new File(DEFAULT_DOWNLOAD_PATH),clase);
                Logger.comunicacion( clase, "Directorio Eliminado >>" + !new File(DEFAULT_DOWNLOAD_PATH).exists());

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(FalloConexionActivity.this, Init.class);
                startActivity(intent);
            }
        });
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(FalloConexionActivity.this, Init.class);
                startActivity(intent);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void mostrarSerialvsVersion() {
        TextView tvSerial = findViewById(R.id.tvSerial);
        TextView tvVersion = findViewById(R.id.tvVersion);
        showVersionSerial(tvVersion,tvSerial);
    }
    @Override
    public void onBackPressed() {
    //Se deja vacio metodo de volver atrás porque se requiere desactivar este botón en la pantalla de inicializacion fallida.
    }


}