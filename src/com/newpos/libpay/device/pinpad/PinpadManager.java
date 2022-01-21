package com.newpos.libpay.device.pinpad;

import static com.flota.keys.InjectMasterKey.MASTERKEYIDX;
import static com.flota.keys.InjectMasterKey.TRACK2KEYIDX;

import com.flota.screen.inputs.ScreenPin;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.SDKException;
import com.pos.device.icc.SlotType;
import com.pos.device.ped.IccOfflinePinApdu;
import com.pos.device.ped.KeySystem;
import com.pos.device.ped.MACMode;
import com.pos.device.ped.Ped;
import com.pos.device.ped.PedRetCode;
import com.pos.device.ped.PinBlockCallback;
import com.pos.device.ped.PinBlockFormat;
import com.pos.device.ped.RsaPinKey;
import com.secure.api.PadView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhouqiang on 2017/3/14.
 *
 * @author zhouqiang
 * 密码键盘管理者
 */

public class PinpadManager {

    public static final String TAG = "PinpadManager";

    /**
     * wait the time get offline pin.
     */
    private static final int TIME_OUT = 70;//Second
    private static PinpadManager instance;

    private PinpadListener listener;
    private String pinCardNo;
    private int timeout;
    private Object offlinePinLock = new Object();
    private int mErrorCode;

    /**
     * get pin status.
     */
    private AtomicBoolean status = new AtomicBoolean(false);

    private PinpadManager() {
    }

    public static PinpadManager getInstance() {
        if (instance == null) {
            instance = new PinpadManager();
        }
        return instance;
    }

    /**
     * 注入主密钥
     *
     * @param info
     * @return
     */
    public static int loadMKey(MasterKeyinfo info) {
        return Ped.getInstance().injectKey(
                PinpadKeytem.getKS(info.getKeySystem()),
                PinpadKeytype.getKT(info.getKeyType()),
                info.getMasterIndex(),
                info.getPlainKeyData());
    }

    /**
     * 注入工作密钥
     *
     * @param info
     * @return
     */
    public static int loadWKey(WorkKeyinfo info) {
        return Ped.getInstance().writeKey(
                PinpadKeytem.getKS(info.getKeySystem()),
                PinpadKeytype.getKT(info.getKeyType()),
                info.getMasterKeyIndex(),
                info.getWorkKeyIndex(),
                info.getMode(),
                info.getPrivacyKeyData());
    }

    /**
     * @param screen
     * @param l
     */
    public void getPin(ScreenPin screen, PinpadListener l) {
        this.listener = l;
        this.timeout = screen.getTimeout();
        this.pinCardNo = screen.getPinCardNo();

        final PinInfo info = new PinInfo();
        if (null == l) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_invoke_para_err);
        } else if (pinCardNo == null || pinCardNo.equals("")) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_invoke_para_err);
            listener.callback(info);
        } else {
            Logger.debug("PinPadManager>>getPin>>");
            final Ped ped = Ped.getInstance();
            pinCardNo = pinCardNo.substring(pinCardNo.length() - 13, pinCardNo.length() - 1);
            pinCardNo = ISOUtil.padleft(pinCardNo, pinCardNo.length() + 4, '0');
            ped.setPinPadView(screen.getPadView());
            new Thread() {
                @Override
                public void run() {
                    ped.getPinBlock(KeySystem.MS_DES,
                            MASTERKEYIDX,
                            PinBlockFormat.PIN_BLOCK_FORMAT_0,
                            "4,5,6,7,8,9,10,11,12",
                            pinCardNo,
                            new PinBlockCallback() {
                                @Override
                                public void onPinBlock(int i, byte[] bytes) {
                                    switch (i) {
                                        case PedRetCode.NO_PIN:
                                            info.setResultFlag(true);
                                            info.setNoPin(true);
                                            break;
                                        case PedRetCode.TIMEOUT:
                                            info.setResultFlag(false);
                                            info.setErrno(Tcode.T_wait_timeout);
                                            break;
                                        case PedRetCode.ENTER_CANCEL:
                                            info.setResultFlag(false);
                                            info.setErrno(Tcode.T_user_cancel_pin_err);
                                            break;
                                        case 0:
                                            info.setResultFlag(true);
                                            info.setPinblock(bytes);
                                            break;
                                        default:
                                            info.setResultFlag(false);
                                            info.setErrno(i);
                                            break;
                                    }
                                    listener.callback(info);
                                }
                            });
                }
            }.start();
        }
    }

    /**
     * @param screen
     * @param l
     */
    public void getOfflinePin(ScreenPin screen, PinpadListener l) {
        this.listener = l;
        final int i = screen.getI();
        final OfflineRSA key = screen.getKey();

        Ped ped = Ped.getInstance();
        final PinInfo info = new PinInfo();
        ped.setPinPadView(screen.getPadView());
        IccOfflinePinApdu apdu = new IccOfflinePinApdu();
        if (i == 1) {
            RsaPinKey rsaPinKey = new RsaPinKey();
            rsaPinKey.setIccrandom(key.getIccRandom());
            rsaPinKey.setModlen(key.getMod().length);
            rsaPinKey.setMod(key.getMod());
            rsaPinKey.setExplen(key.getExp().length);
            rsaPinKey.setExp(key.getExp());
            apdu.setRsakey(rsaPinKey);
        }
        apdu.setCla(0x00);
        apdu.setIns(0x20);
        apdu.setLe(0x00);
        apdu.setLeflg(0x00);
        apdu.setP1(0x00);
        apdu.setP2(i == 1 ? 0x88 : 0x80);
        ped.getOfflinePin(i == 1 ? KeySystem.ICC_CIPHER : KeySystem.ICC_PLAIN,
                ped.getIccSlot(SlotType.USER_CARD),
                "4,5,6,7,8,9,10,11,12",
                apdu,
                new PinBlockCallback() {
                    @Override
                    public void onPinBlock(int i, byte[] bytes) {
                        if (bytes != null) {
                            Logger.debug("getOfflinePin: bytes=" + ISOUtil.byte2hex(bytes));
                        }
                        info.setPinblock(bytes);
                        if (i == PedRetCode.NO_PIN) {
                            info.setNoPin(true);
                        }
                        info.setResultFlag(i == PedRetCode.NO_PIN || i == 0);
                        listener.callback(info);
                    }
                });
    }

    /**
     * 获取加密后的MAC信息
     *
     * @param data   加密源数据
     * @param offset
     * @param len
     * @return 加密后的MAC信息
     */
    public byte[] getMac(byte[] data, int offset, int len) {
        byte[] macIn;
        macIn = new byte[((len + 7) >> 3) << 3];
        System.arraycopy(data, offset, macIn, 0, len);
        byte[] macBlock = Ped.getInstance().getMac(KeySystem.MS_DES, TMConfig.getInstance().getMasterKeyIndex(), MACMode.MAC_MODE_CUP_8, macIn);
        return macBlock;
    }

    /**
     * 中信银行算MAC采用CBC方式
     *
     * @param data
     * @param offset
     * @param len
     * @return
     */
    public byte[] getCITICMac(byte[] data, int offset, int len) {
        byte[] macIn;
        macIn = new byte[((len + 7) >> 3) << 3];
        System.arraycopy(data, offset, macIn, 0, len);
        byte[] macBlock = Ped.getInstance().getMac(KeySystem.MS_DES, TMConfig.getInstance().getMasterKeyIndex(), MACMode.MAC_MODE_CUP, macIn);
        return macBlock;
    }

    /**
     * 获取加密后的磁道信息
     * 磁道加密
     *
     * @param track
     * @return
     */
    public String getEac(int index, String track) {
        int ofs, org_len;
        StringBuffer trackEnc = new StringBuffer(120);
        byte[] bufSrc;
        byte[] bufDest;
        if (track == null || track.equals("")) {
            return null;
        }
        org_len = track.length();//37
        if (((org_len % 2) != 0)) {
            if (track.length() < 17) {
                return null;
            }
            ofs = org_len - 17;
        } else {
            if (track.length() < 18) {
                return null;
            }
            ofs = org_len - 18;
        }
        trackEnc.append(track.substring(0, ofs));
        bufSrc = ISOUtil.str2bcd(track.substring(ofs, ofs + 16), false);
        bufDest = Ped.getInstance().encryptAccount(KeySystem.MS_DES, index, Ped.TDEA_MODE_ECB, bufSrc);
        if (bufDest == null) {
            return null;
        }
        trackEnc.append(ISOUtil.byte2hex(bufDest));
        trackEnc.append(track.substring(ofs + 16, org_len));
        return trackEnc.toString();
    }

    public String encryptTrack2(String track) {

        Logger.debug("Track 2 ", track);

        int ofs, org_len, cont, fill_len = 0;
        int cantFs = 0;
        byte[] bufSrc;
        byte[] bufDest;

        StringBuffer trackEnc = new StringBuffer(120);

        if (track == null || track.equals("")) {
            return null;
        }
        org_len = fill_len = track.length();
        if (((org_len % 2) != 0)) {
            if (track.length() < 17) {
                return null;
            }
            ofs = org_len - 17;
        } else {
            if (track.length() < 18) {
                return null;
            }
            ofs = org_len - 18;
        }

        String trackFormat = ISOUtil.padright(track, 48, 'F');
        bufSrc = ISOUtil.str2bcd(trackFormat, false);

        bufDest = Ped.getInstance().encryptAccount(KeySystem.MS_DES, TRACK2KEYIDX, Ped.TDEA_MODE_ECB, bufSrc);

        if (bufDest == null) {
            return null;
        }
        trackEnc.append(ISOUtil.byte2hex(bufDest));
        Logger.debug("Track 2 encriptado ", trackEnc.toString());
        return trackEnc.toString();
    }

    /**
     * get offline pin.
     *
     * @param tryTime
     * @param keyType
     * @param rsaPinkey
     * @param len
     * @param offlinePinBlcok
     * @return
     */
    public int getOfflinePin(int tryTime, int keyType, long amount, RsaPinKey rsaPinkey, final byte[] len, final byte[] offlinePinBlcok) {
        Ped ped = Ped.getInstance();
        PadView padView = new PadView();
        try {
            ped.setPinEntryTimeout(60);
        } catch (SDKException e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }

        padView.setPinTips("INGRESE PIN OFFLINE ");
        ped.setPinPadView(padView);

        KeySystem ks;
        if (keyType == 1)
            ks = KeySystem.ICC_CIPHER;
        else
            ks = KeySystem.ICC_PLAIN;
        int fd = ped.getIccSlot(SlotType.USER_CARD);
        Logger.debug("slot", "fd=" + fd);
        String pinLenLimit = "4,5,6,7,8,9,10,11,12";

        IccOfflinePinApdu apdu = new IccOfflinePinApdu();
        if (keyType == 1) {
            apdu.setRsakey(rsaPinkey);
        }
        apdu.setCla(0x00);
        apdu.setIns(0x20);
        apdu.setLe(0x00);
        apdu.setLeflg(0x00);
        apdu.setP1(0x00);
        apdu.setP2((keyType == 1) ? 0x88 : 0x80);

        status.set(false);
        mErrorCode = -1;
        ped.getOfflinePin(ks, fd, pinLenLimit, apdu, new PinBlockCallback() {
            @Override
            public void onPinBlock(int result, byte[] pinBlock) {

                if (result != 0) {
                    mErrorCode = result;
                } else { //成功
                    if (pinBlock != null && len != null && offlinePinBlcok != null) {
                        len[0] = (byte) pinBlock.length;
                        System.arraycopy(pinBlock, 0, offlinePinBlcok, 0, len[0]);
                        mErrorCode = 0;
                    }
                }
                status.set(true);
                releaseLock(offlinePinLock);
            }
        });
        tryLock(offlinePinLock, TIME_OUT);

        return mErrorCode;
    }

    /**
     * lock the thread.
     *
     * @param timeOut
     */
    private void tryLock(Object lock, int timeOut) {
        if (lock != null) {
            try {
                synchronized (lock) {
                    lock.wait(timeOut * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
            }
        }
    }

    /**
     * release lock
     */
    private void releaseLock(Object lock) {
        if (lock != null) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }
}
