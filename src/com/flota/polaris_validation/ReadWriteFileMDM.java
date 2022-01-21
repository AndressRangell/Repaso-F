package com.flota.polaris_validation;

import android.os.Build;
import android.os.Environment;
import android.util.Xml;

import com.newpos.libpay.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.flota.actividades.StartAppBANCARD.readWriteFileMDM;


public class ReadWriteFileMDM {

    static String clase = "ReadWriteFileMDM.java";
    private XmlSerializer serializer;
    private FileOutputStream fileos = null;
    private String reverse;
    private String Settle;

    private String reversal;
    private String settle;
    private String isTrans;
    private String initAuto;



    public static final String REVERSE_ACTIVE = "1";
    public static final String SETTLE_ACTIVE = "1";
    public static final String ISTRANSACCION_ACTIVE = "1";
    public static final String ISTRANSACCION_DEACTIVE = "0";
    public static final String REVERSE_DEACTIVE = "0";
    public static final String SETTLE_DEACTIVE = "0";


    public static ReadWriteFileMDM getInstance() {
        if (readWriteFileMDM == null) {
            readWriteFileMDM = new ReadWriteFileMDM();
        }
        readWriteFileMDM.readFileMDM();
        return readWriteFileMDM;
    }

    public String getReverse() {
        return reverse;
    }

    public void setReverse(String reverse) {
        this.reverse = reverse;
    }

    public String getSettle() {
        return Settle;
    }

    public void setSettle(String settle) {
        Settle = settle;
    }


    public void createXML(String name) {

        serializer = Xml.newSerializer();
        File newxmlfile;
        if (Build.MODEL.equals("NEW9220")) {
            newxmlfile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + name + ".xml");
        } else {
            newxmlfile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), name + ".xml");
        }

        try {
            newxmlfile.createNewFile();
        } catch (IOException e) {
            Logger.exception(clase, e);
        }

        try {

            fileos = new FileOutputStream(newxmlfile);

        } catch (FileNotFoundException e) {
            Logger.exception(clase, e);
        }
        try {

            serializer.setOutput(fileos, "UTF-8");

        } catch (IOException e) {
            Logger.exception(clase, e);
        }
    }

    public void setTag(String tag, String value) {
        if (value != null) {
            try {
                serializer.startTag(null, tag);
                serializer.text(value);
                serializer.endTag(null, tag);
            } catch (IOException e) {
                Logger.exception(clase, e);
            }
        }
    }

    public void writeFileMDM(String reversal, String settle) {
        this.reversal = reversal;
        this.settle = settle;

        try {
            cargarInformacionXML();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(clase + " writeFileMDM", e);
        }
    }

    public void writeFileMDM(String isTrans) {
        this.isTrans = isTrans;
        cargarInformacionXML();
    }

    private void cargarInformacionXML() {
        createXML("ConfigDF");

        try {
            serializer.startTag(null, "Configuration");
            serializer.startTag(null, "MdmInstall");

            setTag("isReversal", reversal);
            setTag("isSettle", settle);
            setTag("isTrans", isTrans);
            setTag("initAuto", initAuto);


            serializer.endTag(null, "MdmInstall");
            serializer.endTag(null, "Configuration");

            serializer.endDocument();
            serializer.flush();
            fileos.close();

            readFileMDM();
        } catch (IOException e1) {
            Logger.exception(clase, e1);
        }
    }

    public String getInitAuto() {
        return initAuto;
    }

    public void readFileMDM() {

        String reverse = null,
                settle = null;

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/ConfigDF.xml");


        if (file.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Element documentElement = document.getDocumentElement();
                NodeList sList = documentElement.getElementsByTagName("MdmInstall");

                if (sList != null && sList.getLength() > 0) {
                    for (int i = 0; i < sList.getLength(); i++) {
                        Node node = sList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {

                            node.getAttributes();
                            Element e = (Element) node;
                            NodeList listElements = e.getElementsByTagNameNS(node.getNamespaceURI(), node.getLocalName());

                            //lee cada elemento del state
                            for (int b = 0; b < listElements.getLength(); b++) {

                                String nameEl = listElements.item(b).getNodeName();
                                try {
                                    if (nameEl.equals("isReversal")) {
                                        reverse = listElements.item(b).getTextContent();
                                    }
                                    if (nameEl.equals("isSettle")) {
                                        settle = listElements.item(b).getTextContent();
                                    }

                                    if (nameEl.equals("initAuto")) {
                                        String initActualizacion = listElements.item(b).getTextContent();
                                        if (initActualizacion != null){
                                            this.initAuto = initActualizacion;
                                        }

                                    }
                                } catch (Exception e1) {
                                    Logger.exception(clase, e1);

                                }

                            }
                            if (reverse != null && settle != null) {
                                this.reverse = reverse;
                                Settle = settle;
                            } else {
                                writeFileMDM(getReverse(), getSettle());
                                writeFileMDM(ISTRANSACCION_DEACTIVE);
                            }

                        }
                    }
                }
            } catch (Exception e) {
                Logger.exception(clase, e);
                createXML("ConfigDF");
            }
        } else {
            writeFileMDM(getReverse(), getSettle());
            writeFileMDM(ISTRANSACCION_DEACTIVE);
        }

    }
}
