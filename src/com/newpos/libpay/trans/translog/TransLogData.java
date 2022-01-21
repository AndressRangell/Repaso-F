package com.newpos.libpay.trans.translog;

import java.io.Serializable;

/**
 * 交易日志详情信息类
 *
 * @author zhouqiang
 */
public class TransLogData implements Serializable {

    private String nombreComercio;
    private String phoneTrade;
    private String AID;
    private String AIDName;
    private String addressTrade;
    private int numCuotas;
    private String datePrint;
    private String MsgID;
    private String labelCard;
    private String TypeCoin;
    private String nameCard;
    private int AAC;
    private boolean isNFC;
    private boolean isScan;
    private boolean isICC;
    private String BatchNo;
    private boolean isVoided;
    private String TypeTransVoid;
    private String Pan;
    private String TransEName;
    protected String TipoVenta;
    private String panNormal;
    private String ProcCode;
    private Long Amount;
    private Long OtherAmount;
    private String TraceNo;
    private String LocalTime;
    private boolean fallback;
    private String SettleDate;
    private String typeAccount;
    private String IssuerName;
    private String LocalDate;
    private String ExpDate;
    private String EntryMode;
    private String PanSeqNo;
    private String Nii;
    private String SvrCode;
    private String AcquirerID;
    private String Track2;
    private String RRN;
    private String AuthCode;
    private String RspCode;
    private String TermID;
    private String MerchID;
    private String CurrencyCode;
    private int oprNo;
    private String PIN;
    private byte[] ICCData;
    private String Field60;
    private String Field62;
    private String Field63;

    public String getMsgID() {
        return MsgID;
    }

    public void setMsgID(String msgID) {
        MsgID = msgID;
    }

    public String getDatePrint() {
        return datePrint;
    }

    public void setDatePrint(String datePrint) {
        this.datePrint = datePrint;
    }

    public int getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(int numCuotas) {
        this.numCuotas = numCuotas;
    }

    public String getAddressTrade() {
        return addressTrade;
    }

    public void setAddressTrade(String addressTrade) {
        this.addressTrade = addressTrade;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nameTrade) {
        this.nombreComercio = nameTrade;
    }

    public String getPhoneTrade() {
        return phoneTrade;
    }

    public void setPhoneTrade(String phoneTrade) {
        this.phoneTrade = phoneTrade;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getAIDName() {
        return AIDName;
    }

    public void setAIDName(String AIDName) {
        this.AIDName = AIDName;
    }

    public String getLabelCard() {
        return labelCard;
    }

    public void setLabelCard(String labelCard) {
        this.labelCard = labelCard;
    }

    public String getNameCard() {
        return nameCard;
    }

    public void setNameCard(String nameCard) {
        this.nameCard = nameCard;
    }

    public String getTypeCoin() {
        return TypeCoin;
    }

    public void setTypeCoin(String typeCoin) {
        TypeCoin = typeCoin;
    }

    public int getAAC() {
        return AAC;
    }

    public void setAAC(int AAC) {
        this.AAC = AAC;
    }

    public boolean isNFC() {
        return isNFC;
    }

    public void setNFC(boolean isNFC) {
        this.isNFC = isNFC;
    }

    public boolean isICC() {
        return isICC;
    }

    public void setICC(boolean isICC) {
        this.isICC = isICC;
    }

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public boolean getIsVoided() {
        return isVoided;
    }

    public void setVoided(boolean isVoided) {
        this.isVoided = isVoided;
    }

    public String getTypeTransVoid() {
        return TypeTransVoid;
    }

    public void setTypeTransVoid(String typeTransVoid) {
        TypeTransVoid = typeTransVoid;
    }

    public String getEName() {
        return TransEName;
    }

    public void setEName(String eName) {
        TransEName = eName;
    }

    public String getPan() {
        return Pan;
    }

    public void setPan(String pan) {
        Pan = pan;
    }

    public String getPanNormal() {
        return panNormal;
    }

    public void setPanNormal(String panNormal) {
        this.panNormal = panNormal;
    }

    public String getProcCode() {
        return ProcCode;
    }

    public void setProcCode(String procCode) {
        ProcCode = procCode;
    }

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }

    public Long getOtherAmount() {
        return OtherAmount;
    }

    public void setOtherAmount(Long otherAmount) {
        OtherAmount = otherAmount;
    }

    public String getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(String traceNo) {
        TraceNo = traceNo;
    }

    public String getLocalTime() {
        return LocalTime;
    }

    public void setLocalTime(String localTime) {
        LocalTime = localTime;
    }

    public String getLocalDate() {
        return LocalDate;
    }

    public void setLocalDate(String localDate) {
        LocalDate = localDate;
    }

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    public String getSettleDate() {
        return SettleDate;
    }

    public void setSettleDate(String settleDate) {
        SettleDate = settleDate;
    }

    public String getEntryMode() {
        return EntryMode;
    }

    public void setEntryMode(String entryMode) {
        EntryMode = entryMode;
    }

    public String getPanSeqNo() {
        return PanSeqNo;
    }

    public void setPanSeqNo(String panSeqNo) {
        PanSeqNo = panSeqNo;
    }

    public String getNii() {
        return Nii;
    }

    public void setNii(String nii) {
        Nii = nii;
    }

    public String getSvrCode() {
        return SvrCode;
    }

    public void setSvrCode(String svrCode) {
        SvrCode = svrCode;
    }

    public String getAcquirerID() {
        return AcquirerID;
    }

    public void setAcquirerID(String acquirerID) {
        AcquirerID = acquirerID;
    }

    public String getTrack2() {
        return Track2;
    }

    public void setTrack2(String track2) {
        Track2 = track2;
    }

    public String getRRN() {
        return RRN;
    }

    public void setRRN(String rRN) {
        RRN = rRN;
    }

    public String getAuthCode() {
        return AuthCode;
    }

    public void setAuthCode(String authCode) {
        AuthCode = authCode;
    }

    public String getRspCode() {
        return RspCode;
    }

    public void setRspCode(String rspCode) {
        RspCode = rspCode;
    }

    public String getTermID() {
        return TermID;
    }

    public void setTermID(String termID) {
        TermID = termID;
    }

    public String getMerchID() {
        return MerchID;
    }

    public void setMerchID(String merchID) {
        MerchID = merchID;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    public int getOprNo() {
        return oprNo;
    }

    public void setOprNo(int oprNo) {
        this.oprNo = oprNo;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String pin) {
        PIN = pin;
    }

    public byte[] getICCData() {
        return ICCData;
    }

    public void setICCData(byte[] iCCData) {
        ICCData = iCCData;
    }

    public String getField60() {
        return Field60;
    }

    public void setField60(String field60) {
        Field60 = field60;
    }

    public String getField62() {
        return Field62;
    }

    public void setField62(String field62) {
        Field62 = field62;
    }

    public String getField63() {
        return Field63;
    }

    public void setField63(String field63) {
        Field63 = field63;
    }

    public String getIssuerName() {
        return IssuerName;
    }

    public void setIssuerName(String issuerName) {
        IssuerName = issuerName;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public String getTipoVenta() {
        return TipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        TipoVenta = tipoVenta;
    }

    /**
     * BANCARD
     */
    private long ammountCashOver;
    private long montoFijo;
    private String Field02;
    private String Field19;
    private String Field48;
    private String field55;
    private String field61;
    private String ARQC;
    private String TC;
    private String TVR;
    private String TSI;
    private String numCuotasDeferred;
    private String tipoMontoFijo;
    private String MerchantType;
    private boolean isPinExist;
    private boolean isDebit;
    private String nroCargo;
    private String codigoDelNegocio;
    private String tipoTarjeta; //C = Credito, D = Debito - Ultima letra del subCampo89
    private String additionalAmount;
    //private String pinOffline;

    public long getAmmountCashOver() {
        return ammountCashOver;
    }

    public void setAmmountCashOver(long ammountCashOver) {
        this.ammountCashOver = ammountCashOver;
    }

    public long getMontoFijo() {
        return montoFijo;
    }

    public void setMontoFijo(long montoFijo) {
        this.montoFijo = montoFijo;
    }

    public String getField02() {
        return Field02;
    }

    public void setField02(String field02) {
        Field02 = field02;
    }

    public String getField19() {
        return Field19;
    }

    public void setField19(String field19) {
        Field19 = field19;
    }

    public String getField48() {
        return Field48;
    }

    public void setField48(String field48) {
        Field48 = field48;
    }

    public String getField55() {
        return field55;
    }

    public void setField55(String field55) {
        this.field55 = field55;
    }

    public String getField61() {
        return field61;
    }

    public void setField61(String field61) {
        this.field61 = field61;
    }

    public String getARQC() {
        return ARQC;
    }

    public void setARQC(String ARQC) {
        this.ARQC = ARQC;
    }

    public String getTC() {
        return TC;
    }

    public void setTC(String TC) {
        this.TC = TC;
    }

    public String getTVR() {
        return TVR;
    }

    public void setTVR(String TVR) {
        this.TVR = TVR;
    }

    public String getTSI() {
        return TSI;
    }

    public void setTSI(String TSI) {
        this.TSI = TSI;
    }

    public String getNumCuotasDeferred() {
        return numCuotasDeferred;
    }

    public void setNumCuotasDeferred(String numCuotasDeferred) {
        this.numCuotasDeferred = numCuotasDeferred;
    }

    public boolean isVoided() {
        return isVoided;
    }

    public String getTransEName() {
        return TransEName;
    }

    public void setTransEName(String transEName) {
        TransEName = transEName;
    }

    public String getTipoMontoFijo() {
        return tipoMontoFijo;
    }

    public void setTipoMontoFijo(String tipoMontoFijo) {
        this.tipoMontoFijo = tipoMontoFijo;
    }

    public String getMerchantType() {
        return MerchantType;
    }

    public void setMerchantType(String merchantType) {
        MerchantType = merchantType;
    }

    public boolean isPinExist() {
        return isPinExist;
    }

    public void setPinExist(boolean pinExist) {
        isPinExist = pinExist;
    }

    public boolean isDebit() {
        return isDebit;
    }

    public void setDebit(boolean debit) {
        isDebit = debit;
    }

    public String getNroCargo() {
        return nroCargo;
    }

    public void setNroCargo(String nroCargo) {
        this.nroCargo = nroCargo;
    }

    public String getCodigoDelNegocio() {
        return codigoDelNegocio;
    }

    public void setCodigoDelNegocio(String codigoDelNegocio) {
        this.codigoDelNegocio = codigoDelNegocio;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getAdditionalAmount() {
        return additionalAmount;
    }

    public void setAdditionalAmount(String additionalAmount) {
        this.additionalAmount = additionalAmount;
    }
    /*

    public String getPinOffline() {
        return pinOffline;
    }

    public void setPinOffline(String pinOffline) {
        this.pinOffline = pinOffline;
    }

     */
}
