/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LankaOCR.OCRActions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

    public String PerformOcr(String filePath) {

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

    public void NormalizeOutputText(File ocrOutputString) {

        String innerSpanContent, innerText, normalizedInnerText;

        try {
            Document inputHtmlDoc = Jsoup.parse(ocrOutputString, "UTF-8");
            PrintWriter writer = new PrintWriter(ocrOutputString, "UTF-8");

            //Choose each word in the output
            for (Element span : inputHtmlDoc.select("span.ocrx_word")) {

                innerSpanContent = span.html();
                innerText = span.text();
                normalizedInnerText = applyVowelNormalizationRules(innerText); // Apply Vowel Normalization rules
                normalizedInnerText = applyConsonentNormalizationRules(innerText);
                innerSpanContent = innerSpanContent.replace(innerText, normalizedInnerText);
                span.html(innerSpanContent);

            }

            writer.write(inputHtmlDoc.html());
            writer.flush();
            writer.close();

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }

//    return null;
    }

    private String applyVowelNormalizationRules(String wordString) {

        String modifiedWordString = wordString;

        /**
         * Sinhala Code point range in decimal 3456-3583 *
         */
        // Start Replace the Vowels with modifies to the proper character
        if (wordString.charAt(0) == 3461 && wordString.charAt(1) == 3535) { //          SINHALA LETTER AAYANNA

            modifiedWordString = wordString.replace(Character.toString(wordString.charAt(0)), Character.toString((char) 3462));
            StringBuilder tempWordString1 = new StringBuilder(modifiedWordString);
            tempWordString1.deleteCharAt(1);
            modifiedWordString = tempWordString1.toString();

        } else if (wordString.charAt(0) == 3461 && wordString.charAt(1) == 3536) { //     SINHALA LETTER AEYANNA

            modifiedWordString = wordString.replace(Character.toString(wordString.charAt(0)), Character.toString((char) 3463));
            StringBuilder tempWordString2 = new StringBuilder(modifiedWordString);
            tempWordString2.deleteCharAt(1);
            modifiedWordString = tempWordString2.toString();

        } else if (wordString.charAt(0) == 3461 && wordString.charAt(1) == 3537) { //	SINHALA LETTER AEEYANNA

            modifiedWordString = wordString.replace(Character.toString(wordString.charAt(0)), Character.toString((char) 3464));
            StringBuilder tempWordString3 = new StringBuilder(modifiedWordString);
            tempWordString3.deleteCharAt(1);
            modifiedWordString = tempWordString3.toString();

        } else if (wordString.charAt(0) == 3467 && wordString.charAt(1) == 3551) { //   SINHALA LETTER UUYANNA

            modifiedWordString = wordString.replace(Character.toString(wordString.charAt(0)), Character.toString((char) 3468));
            StringBuilder tempWordString4 = new StringBuilder(modifiedWordString);
            tempWordString4.deleteCharAt(1);
            modifiedWordString = tempWordString4.toString();

        } else if (wordString.charAt(0) == 3545 && wordString.charAt(1) == 3473) { // 	SINHALA LETTER AIYANNA

            modifiedWordString = wordString.replace(Character.toString(wordString.charAt(1)), Character.toString((char) 3475));
            StringBuilder tempWordString5 = new StringBuilder(modifiedWordString);
            tempWordString5.deleteCharAt(0);
            modifiedWordString = tempWordString5.toString();

        } else if (wordString.charAt(0) == 3476 && wordString.charAt(1) == 3551) { //  SINHALA LETTER AUYANNA

            modifiedWordString = wordString.replace(Character.toString(wordString.charAt(0)), Character.toString((char) 3478));
            StringBuilder tempWordString6 = new StringBuilder(modifiedWordString);
            tempWordString6.deleteCharAt(1);
            modifiedWordString = tempWordString6.toString();
        }

        //TODO : add  follwoing Vowel rules Later 
        /*
        	SINHALA LETTER IRUYANNA
                SINHALA LETTER IRUUYANNA
        	SINHALA LETTER ILUUYANNA
        
         */
        // End Replace the Vowels with modifies to the proper character
        return modifiedWordString;
    }

    private String applyConsonentNormalizationRules(String innerText) {

        // Sinhala Consonent character range between 3482 - 3526
        int startingChar, middleChar, endChar;
        char tempChar, firstChar, secondChar, thirdChar;
        char[] innertTextCharArray = innerText.toCharArray();

        for (int i = 0; i < innertTextCharArray.length -3;) {
            
            
            startingChar = innerText.charAt(i);
            middleChar = innerText.charAt(i + 1);
            endChar = innerText.charAt(i + 2);
            char[] tempCharArray = innerText.toCharArray();
            
            log.info(innerText+"- Length ="+innertTextCharArray.length+"  current processing Char : "+ startingChar );
            
            if (startingChar > 3535 && startingChar < 3571) {
                switch (startingChar) {

                    case 3545: //SINHALA VOWEL SIGN KOMBUVA

                        tempChar = tempCharArray[i + 2];
                        firstChar = tempCharArray[i];
                        secondChar = tempCharArray[i + 1];
                        thirdChar = tempCharArray[i + 2];

                        if (middleChar == 3545) { //SINHALA VOWEL SIGN KOMBU DEKA 

                            //switch characters
                            tempCharArray[i] = secondChar;
                            tempCharArray[i + 1] = (char) 3547;
                            String tempString = String.valueOf(tempCharArray);
                            StringBuilder tempOutputString = new StringBuilder(tempString);
                            tempOutputString.deleteCharAt(i + 3);
                            innerText = tempOutputString.toString();
                            i = i + 3;

                        } else if (middleChar >= 3482 && middleChar <= 3526) {

                            tempCharArray[i] = secondChar;
                            tempCharArray[i + 1] = (char) firstChar;
                            innerText = String.valueOf(tempCharArray);
                            i = i + 2;
                        }
                }

            } else {
                
                i++;

            }

        }

        return innerText;
    }

    private static String escapeNonAscii(String str) {

        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
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
