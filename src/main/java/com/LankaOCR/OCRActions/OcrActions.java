/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LankaOCR.OCRActions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author kasun
 */
public class OcrActions {
    
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    public String PerformOcr(String filePath){
    
     String result = null;   
     File imageFile = new File(filePath);

        
        Tesseract instance = new Tesseract();// JNA Interface Mapping
        instance.setLanguage("sin");
        instance.setHocr(true);
        instance.setDatapath(".");

        try {
            result = instance.doOCR(imageFile);
            
             } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    
        return result;
    }
    
    public void NormalizeOutputText(File ocrOutputString){
    
    String innerSpanContent,innerText,normalizedInnerText;        
//    File inputHtml = new File(ocrOutputString);
    OutputStreamWriter tempFileWriter;
//    List<String> outputWordList = new ArrayList<>();
    
    
        try {
            Document inputHtmlDoc = Jsoup.parse(ocrOutputString, "UTF-8");
            
            for(Element span : inputHtmlDoc.select("span.ocrx_word")){
                innerSpanContent = span.html();
                innerText=span.text();
                log.info(escapeNonAscii(innerText)+"  "+innerText.getBytes("UTF-8"));
//                outputWordList.add(Arrays.toString(innerText.getBytes("UTF-8")));
            }
            
            
        } catch (IOException ex) {
            log.error(ex.getMessage(),ex);
        }
    
    
    
//    return null;
    }
    
    private static String escapeNonAscii(String str) {

  StringBuilder retStr = new StringBuilder();
  for(int i=0; i<str.length(); i++) {
    int cp = Character.codePointAt(str, i);
    int charCount = Character.charCount(cp);
    if (charCount > 1) {
      i += charCount - 1; // 2.
      if (i >= str.length()) {
        throw new IllegalArgumentException("truncated unexpectedly");
      }
    }

    if (cp < 128) {
      retStr.appendCodePoint(cp);
    } else {
      retStr.append(String.format("\\u%x", cp));
    }
  }
  return retStr.toString();
}
    
    
}
