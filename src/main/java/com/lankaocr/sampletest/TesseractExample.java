/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lankaocr.sampletest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import net.sourceforge.tess4j.*;

public class TesseractExample {

    public static void main(String[] args) throws IOException {

        File imageFile = new File("SinhalaTestInput.tif");

        System.out.println(imageFile.getAbsolutePath());
        Tesseract instance = new Tesseract();// JNA Interface Mapping
        instance.setLanguage("sin");
        instance.setHocr(true);
        instance.setDatapath(".");

        try {
            String result = instance.doOCR(imageFile);
            OutputStreamWriter writer;
            writer = new OutputStreamWriter(new FileOutputStream("a.doc"), StandardCharsets.UTF_8);
            writer.write(result);

            writer.close();
//            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
