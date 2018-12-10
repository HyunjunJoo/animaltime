package com.example.joohj.animaltime;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


public class Forecast {
    private String pop;//강수 확률
    private String pty;//강수 형태
    private String sky;//하늘 상태
    private String t3h;//3시간 기온

    public Forecast() {
        this.pop = null;
        this.pty = null;
        this.sky = null;
        this.t3h = null;
    }

    public Forecast(String pop, String pty, String sky, String t3h) {
        this.pop = pop;
        this.pty = pty;
        this.sky = sky;
        this.t3h = t3h;
    }

    public void parseXML(String xml){
        try{
            InputSource input = new InputSource(new StringReader(xml));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList category = (NodeList)xpath.evaluate("//item/category", document, XPathConstants.NODESET);
            NodeList fcstValue = (NodeList)xpath.evaluate("//item/fcstValue", document, XPathConstants.NODESET);

            for (int i = 0; i < category.getLength(); i++) {
                String cat = category.item(i).getTextContent();
                String val = fcstValue.item(i).getTextContent();
                Log.d("parser", ">>> category : " + cat + " | value : " + val);

                if(cat.equals("POP")) {
                    setPop(val);
                }
                else if(cat.equals("PTY")) {
                    setPty(val);
                }
                else if(cat.equals("SKY")) {
                    setSky(val);
                }
                else if(cat.equals("T3H")) {
                    setT3h(val);
                }
            }
        }
        catch(Exception e) {

        }
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public String getPty() {
        return pty;
    }

    public void setPty(String pty) {
        this.pty = pty;
    }

    public String getSky() {
        return sky;
    }

    public void setSky(String sky) {
        this.sky = sky;
    }

    public String getT3h() {
        return t3h;
    }

    public void setT3h(String t3h) {
        this.t3h = t3h;
    }
}
