/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LankaOCR.OCRActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author kasun
 */
public class OcrActions {
    
    public String PerformOcr(String filePath){
    
     String result = null;   
     File imageFile = new File(filePath);

        System.out.println(imageFile.getAbsolutePath());
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
    
}
