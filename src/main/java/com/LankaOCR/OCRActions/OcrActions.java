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

    public String performOcr(String filePath) {

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

    public void normalizeOutputText(File ocrOutputString) {

        String innerSpanContent, innerText, normalizedInnerText;

        try {
            Document inputHtmlDoc = Jsoup.parse(ocrOutputString, "UTF-8");
            PrintWriter writer = new PrintWriter(ocrOutputString, "UTF-8");

            //Choose each word in the output
            for (Element span : inputHtmlDoc.select("span.ocrx_word")) {

                innerSpanContent = span.html();
                innerText = span.text();
                normalizedInnerText = applyVowelNormalizationRules(innerText); // Apply Vowel Normalization rules
                normalizedInnerText = applyConsonentNormalizationRules(normalizedInnerText);
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

        //TODO : add  following Vowel rules Later 
        /*
        	SINHALA LETTER IRUYANNA
                SINHALA LETTER IRUUYANNA
        	SINHALA LETTER ILUUYANNA
        
         */
        // End Replace the Vowels with modifies to the proper character
        return modifiedWordString;
    }

    private String applyConsonentNormalizationRules(String innerText) {

        log.info("Currently Processing for consonent norm: " + innerText);

        int lengthOfString = innerText.length();

        for (int currentPos = 0; currentPos < lengthOfString;) {

            if (innerText.charAt(currentPos) == 3545) { // 	SINHALA VOWEL SIGN KOMBUVA

                if (currentPos + 3 <= lengthOfString) { // String of 4 chars starting from kombuwa
                    if ((innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526)
                            && innerText.charAt(currentPos + 2) == 3535 && innerText.charAt(currentPos + 3) == 3530) { // kombuwa Consonant alapilla hal kireema

                        log.info("The string before modification : " + innerText);

                        innerText = innerText.replace(Character.toString(innerText.charAt(currentPos + 3)), Character.toString((char) 3549)); 
                        innerText = deleteCharAt(innerText, currentPos);
                        innerText = deleteCharAt(innerText, currentPos + 1);

                        log.info("The string after modification : " + innerText);

                        lengthOfString = innerText.length();
                        currentPos += 2;
                    } else {

                        currentPos++; //TODO implement Later
                    }
                } else if(currentPos + 2 <= lengthOfString){ // string of 3 chars starting from kombuwa
                    
                     if ((innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526)
                            && innerText.charAt(currentPos + 2) == 3535) {
                         
                     }
                
                
                } 
                
                else {

                    currentPos++; //TODO implement Later
                }
            } else {

                currentPos++; //TODO implement Later
            }

        }

        return innerText;
    }

    static String swapCharacters(String str, int i, int j) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(i, str.charAt(j));
        sb.setCharAt(j, str.charAt(i));
        return sb.toString();
    }

    static String insertCharAt(String inputString, char inputChar, int charPosition) {
        StringBuilder sb = new StringBuilder(inputString);
        sb.insert(charPosition, inputChar);
        return sb.toString();

    }

    static String deleteCharAt(String inputString, int charPosition) {

        StringBuilder sb = new StringBuilder(inputString);
        sb.deleteCharAt(charPosition);
        return sb.toString();

    }

    static String replaceCharAt(String inputString, int charPosition, int inputChar) {

        return inputString.replace(Character.toString(inputString.charAt(charPosition)), Character.toString((char) inputChar));

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
