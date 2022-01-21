package com.newpos.libpay;

import android.app.Activity;
import android.content.Context;

import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.transactions.Reversal.ReversalTransAuto;
import com.flota.transactions.Settle.Settle;
import com.flota.transactions.anulacion.Anulacion;
import com.flota.transactions.consulta.ConsultaDeSaldo;
import com.flota.transactions.echotest.EchoTest;
import com.flota.transactions.inyeccion.InyeccionLlave;
import com.flota.transactions.venta.Venta;
import com.flota.transactions.venta.VentaCuotas;
import com.flota.transactions.venta.VentaManual;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.paras.EmvAidInfo;
import com.newpos.libpay.paras.EmvCapkInfo;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.presenter.TransUIImpl;
import com.newpos.libpay.presenter.TransView;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKManager;
import com.pos.device.SDKManagerCallback;
import com.wposs.flota.R;

/**
 * Created by zhouqiang on 2017/4/25.
 *
 * @author zhouqiang
 * 支付sdk管理者
 */
public class PaySdk {

    /**
     * 单例
     */
    private static PaySdk mInstance = null;
    /**
     * 标记sdk环境前端是否进行初始化操作
     */
    private static boolean isInit = false;
    String clase = "PaySdk.java";
    /**
     * 上下文对象，用于获取相关资源和使用其相应方法
     */
    private Context mContext = null;
    /**
     * 获前端段activity对象，主要用于扫码交易
     */
    private Activity mActivity = null;
    /**
     * MVP交媾P层接口，用于对m和v的交互
     */
    private TransPresenter presenter = null;
    /**
     * 初始化PaySdk环境的回调接口
     */
    private PaySdkListener mListener = null;
    /**
     * PaySdk产生的相关文件的保存路径
     * 如代码不进行设置，默认使用程序data分区
     *
     * @link @{@link String}
     */
    private String cacheFilePath = null;
    /**
     * 终端参数文件路径,用于设置一些交易中的偏好属性
     * 如代码不进行设置，默认使用程序自带配置文件
     *
     * @link @{@link String}
     */
    private String paraFilepath = null;

    private PaySdk() {
    }

    public static PaySdk getInstance() {
        if (mInstance == null) {
            mInstance = new PaySdk();
        }
        return mInstance;
    }

    public Context getContext() throws PaySdkException {
        if (this.mContext == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }
        return mContext;
    }

    public PaySdk setActivity(Activity activity) {
        this.mActivity = activity;
        return mInstance;
    }

    public String getParaFilepath() {
        return this.paraFilepath;
    }

    public PaySdk setListener(PaySdkListener listener) {
        this.mListener = listener;
        return mInstance;
    }

    public void init(Context context) throws PaySdkException {
        this.mContext = context;
        this.init();
    }

    public void init(Context context, PaySdkListener listener) throws PaySdkException {
        this.mContext = context;
        this.mListener = listener;
        this.init();
    }

    public void init() throws PaySdkException {
        Logger.info("init->start.....");
        if (this.mContext == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }

        if (this.paraFilepath == null || !this.paraFilepath.endsWith("properties")) {
            this.paraFilepath = TMConstants.DEFAULTCONFIG;
        }

        if (this.cacheFilePath == null) {
            this.cacheFilePath = mContext.getFilesDir() + "/";
        } else if (!this.cacheFilePath.endsWith("/")) {
            this.cacheFilePath += "/";
        }


        TMConfig.setRootFilePath(this.cacheFilePath);
        Logger.info("init->paras files path:" + this.paraFilepath);
        Logger.info("init->cache files will be saved in:" + this.cacheFilePath);
        Logger.info("init->pay sdk will run based on:" + (TMConfig.getInstance().getBankid() == 1 ? "UNIONPAY" : "CITICPAY"));
        if (!TMConfig.getInstance().isOnline()) {
            PAYUtils.copyAssetsToData(this.mContext, EmvAidInfo.FILENAME);
            PAYUtils.copyAssetsToData(this.mContext, EmvCapkInfo.FILENAME);
        }
        SDKManager.init(mContext, new SDKManagerCallback() {
            @Override
            public void onFinish() {
                isInit = true;
                Logger.info("init->success");
                if (mListener != null) {
                    mListener.success();
                }
            }
        });
    }

    /**
     * 释放卡片驱动资源
     */
    public void releaseCard() {
        if (isInit) {
            CardManager.getInstance(0).releaseAll();
        }
    }

    /**
     * 释放sdk环境资源
     */
    public void exit() {
        if (isInit) {
            SDKManager.release();
            isInit = false;
        }
    }

    public void startTrans(String transType, String monto, TransView tv, Context ctx) throws PaySdkException {
        if (this.mActivity == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }
        TransInputPara para = new TransInputPara();
        para.setTransUI(new TransUIImpl(mActivity, tv));
        para.setTransType(transType);

        switch (transType) {
            case Trans.Type.ECHO_TEST:
                para.setNeedOnline(true);
                para.setNeedPrint(false);
                para.setNeedPass(false);
                para.setEmvAll(false);
                presenter = new EchoTest(ctx, Trans.Type.ECHO_TEST, para, true);
                break;

            case Trans.Type.VENTA:
                para.setNeedOnline(true);
                para.setNeedPrint(false);
                para.setNeedConfirmCard(false);
                para.setNeedPass(true);
                para.setNeedAmount(false);
                para.setEmvAll(true);
                para.setNeedOtherAmount(DefinesBANCARD.DATO_HABILITADO);
                presenter = new Venta(ctx, Trans.Type.VENTA, para);
                break;

            case Trans.Type.VENTAMANUAL:
                para.setNeedOnline(true);
                para.setNeedPrint(true);
                para.setNeedConfirmCard(false);
                para.setNeedPass(true);
                para.setNeedAmount(false);
                para.setEmvAll(true);
                para.setNeedOtherAmount(DefinesBANCARD.DATO_NO_HABILITADO);
                presenter = new VentaManual(ctx, Trans.Type.VENTAMANUAL, para);
                break;

            case Trans.Type.VENTACUOTAS:
                para.setNeedOnline(true);
                para.setNeedPrint(true);
                para.setNeedConfirmCard(false);
                para.setNeedPass(true);
                para.setNeedAmount(true);
                para.setEmvAll(true);
                presenter = new VentaCuotas(ctx, transType, monto, para);
                break;

            case Trans.Type.SETTLE:
                para.setNeedOnline(false);
                para.setNeedPrint(true);
                para.setNeedPass(false);
                para.setEmvAll(false);
                presenter = new Settle(ctx, transType, para);

                break;

            case Trans.Type.AUTO_SETTLE:
                para.setNeedOnline(false);
                para.setNeedPrint(true);
                para.setNeedPass(false);
                para.setEmvAll(false);
                presenter = new Settle(ctx, Trans.Type.AUTO_SETTLE, para);

                break;

            case Trans.Type.CONSULTA_SALDO:
                para.setNeedConfirmCard(false);
                para.setNeedOnline(true);
                para.setNeedPass(true);
                para.setNeedAmount(false);
                para.setEmvAll(true);
                presenter = new ConsultaDeSaldo(ctx, Trans.Type.CONSULTA_SALDO, para);
                break;

            case Trans.Type.ANULACION:
                para.setNeedConfirmCard(false);
                para.setNeedOnline(true);
                para.setNeedPass(true);
                para.setNeedAmount(false);
                para.setEmvAll(true);
                para.setNeedAmount(false);
                para.setNeedPrint(true);
                presenter = new Anulacion(ctx, Trans.Type.ANULACION, para);
                break;

            case Trans.Type.INYECCION:
                para.setNeedOnline(true);
                para.setNeedPrint(false);
                para.setNeedPass(false);
                para.setEmvAll(false);
                presenter = new InyeccionLlave(ctx, Trans.Type.INYECCION, para);
                break;

            case Trans.Type.REVERSAL:
                para.setNeedOnline(true);
                para.setNeedPrint(false);
                para.setNeedPass(false);
                para.setEmvAll(false);
                presenter = new ReversalTransAuto(ctx, Trans.Type.REVERSAL, Integer.parseInt(mContext.getResources().getString(R.string.timerDataConfig)), para);
                break;

            default:
                break;
        }

        if (isInit) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        presenter.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            throw new PaySdkException(PaySdkException.NOT_INIT);
        }
    }
}
