package com.flota.keys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wposs.flota.R;

import cn.desert.newpos.payui.UIUtils;

public class PwMasterKey extends AppCompatActivity {

    private EditText etPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_master_key);

        etPw = findViewById(R.id.et_pw_mk);
        Button btnOk = findViewById(R.id.btn_conf_mon);
        Button btnCnl = findViewById(R.id.btn_cancel_mon);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPw.getText().length() != 0) {
                    Intent intent = new Intent();
                    intent.setClass(PwMasterKey.this, InjectMasterKey.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("pw", etPw.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else{
                    UIUtils.toast(PwMasterKey.this, R.drawable.ic_redinfonet, getString(R.string.err_msg_pwmk), Toast.LENGTH_SHORT);
                }
            }
        });

        btnCnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // this method is empty
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        etPw.setText("");
    }
}
