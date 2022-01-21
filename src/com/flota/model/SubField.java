package com.flota.model;

import com.newpos.libpay.utils.ISOUtil;

public class SubField {
    String fieldId;
    String fieldData;


    public SubField(String idCampo, String dataCampo) {
        this.fieldId = ISOUtil.padleft(idCampo, 2, '0');
        this.fieldData = dataCampo;
    }


    public String getFieldId() {
        return fieldId;
    }


    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldData() {
        return fieldData;
    }


    public void setFieldData(String fieldData) {
        this.fieldData = fieldData;
    }


}
