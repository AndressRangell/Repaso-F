package com.newpos.libpay.trans.manager;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.printer.Printer;

import java.util.List;

/**
 * Created by zhouqiang on 2017/3/31.
 * 结算交易处理类
 * @author zhouqiang
 */
@Deprecated
public class SettleTrans extends Trans implements TransPresenter{

    private int sumCount = 0 ;

    public SettleTrans(Context ctx, String transEname , TransInputPara p) {
        super(ctx, transEname);
        iso8583.setHasMac(false);
        para = p ;
        transUI = para.getTransUI() ;
        TransEName = transEname ;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
/**
        transUI.handling(timeout , Tcode.Status.settling_start);
        settle();
*/
    }





}
