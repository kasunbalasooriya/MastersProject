/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lankaocr.actions;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author kasun
 */
public class OcrActions {

    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private char[] charSet;

    public String performOcr(String filePath) {

        String hocrOutput = null;
        File imageFile = new File(filePath);

        Tesseract hocrInstance = new Tesseract();// JNA Interface Mapping
        hocrInstance.setLanguage("sin+sin1");
        hocrInstance.setHocr(true);
        hocrInstance.setDatapath(".");

        try {
            hocrOutput = hocrInstance.doOCR(imageFile);

        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

        return hocrOutput;
    }


    public String returnTextOutput(String filePath) {

        String textOutput = null;
        File imageFile = new File(filePath);

        Tesseract textInstance = new Tesseract();// JNA Interface Mapping
        textInstance.setLanguage("sin");
        textInstance.setDatapath(".");

        try {
            textOutput = textInstance.doOCR(imageFile);

        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

        return textOutput;

    }


    public void normalizeOutputText(File ocrOutputString) {

        String innerSpanContent;
        String innerText;
        String normalizedInnerText;

        try {
            Document inputHtmlDoc = Jsoup.parse(ocrOutputString, "UTF-8");
            PrintWriter writer = new PrintWriter(ocrOutputString, "UTF-8");

            //Choose each word in the output
            for (Element span : inputHtmlDoc.select("span.ocrx_word")) {

                innerSpanContent = span.html();
                innerText = span.text();
                normalizedInnerText = applyVowelNormalizationRules(innerText); // Apply Vowel Normalization rules
                normalizedInnerText = applyConsonantNormalizationRules(normalizedInnerText); // Apply Consonant Normalization rules
                normalizedInnerText = applySpecialConsonantRules(normalizedInnerText);
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

    public String applyVowelNormalizationRules(String wordString) {

        // TODO : Add rule to drop chars before a vowel in a word

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

    public String applyConsonantNormalizationRules(String innerText) {

        // TODO : Add rule to correct kroo


        int lengthOfString = innerText.length();

        for (int currentPos = 0; currentPos < lengthOfString; ) {

            if (innerText.charAt(currentPos) == 3545) { // SINHALA VOWEL SIGN KOMBUVA

                if (currentPos + 3 <= lengthOfString) { // String of 4 chars starting from kombuwa
                    if ((innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526)
                            && innerText.charAt(currentPos + 2) == 3535 && innerText.charAt(currentPos + 3) == 3530) { // kombuwa Consonant alapilla hal kireema

                        innerText = insertCharAt(innerText, (char) 3549, currentPos + 3);
                        innerText = deleteCharAt(innerText, currentPos);
                        innerText = deleteCharAt(innerText, currentPos + 1);
                        innerText = deleteCharAt(innerText, currentPos + 2);

                        lengthOfString = innerText.length();
                        currentPos += 2;

                    } else if (currentPos + 2 <= lengthOfString) { // string of 3 chars starting from kombuwa

                        if ((innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526)
                                && innerText.charAt(currentPos + 2) == 3535) { // kombuwa consonant and adapilla

                            innerText = insertCharAt(innerText, (char) 3548, currentPos + 2);
                            innerText = deleteCharAt(innerText, currentPos);
                            innerText = deleteCharAt(innerText, currentPos + 2);

                            lengthOfString = innerText.length();
                            currentPos += 2;

                        } else if ((innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526)
                                && (innerText.charAt(currentPos + 2) == 3551 || innerText.charAt(currentPos + 2) == 3571)) { // kombuwa consonant and gayanu kiththa

                            innerText = insertCharAt(innerText, (char) 3550, currentPos + 2);
                            innerText = deleteCharAt(innerText, currentPos);
                            innerText = deleteCharAt(innerText, currentPos + 2);

                            lengthOfString = innerText.length();
                            currentPos += 2;

                        } else if (innerText.charAt(currentPos + 1) == 3545
                                && (innerText.charAt(currentPos + 2) >= 3482 && innerText.charAt(currentPos + 2) <= 3526)) { // kombuwa combuwa and consonant

                            innerText = insertCharAt(innerText, (char) 3547, currentPos + 3);
                            innerText = deleteCharAt(innerText, currentPos);
                            innerText = deleteCharAt(innerText, currentPos);

                            lengthOfString = innerText.length();
                            currentPos += 2;

                        } else if ((innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526)
                                && (innerText.charAt(currentPos + 2) == 3551 || innerText.charAt(currentPos + 2) == 3530)) { // kombuwa consonant and hal kireema

                            innerText = insertCharAt(innerText, (char) 3546, currentPos + 2);
                            innerText = deleteCharAt(innerText, currentPos);
                            innerText = deleteCharAt(innerText, currentPos + 2);

                            lengthOfString = innerText.length();
                            currentPos += 2;

                        } else if (innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526) { // kombuwa and consonant

                            innerText = swapCharacters(innerText, currentPos, currentPos + 1);
                            currentPos += 2;

                        } else {

                            currentPos++;

                        }

                    } else if (currentPos + 1 <= lengthOfString) { // string of 2 chars tarting from kombuwa

                        if (innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526) { // kombuwa and consonant

                            innerText = swapCharacters(innerText, currentPos, currentPos + 1);
                            currentPos += 2;

                        } else {
                            currentPos++;
                        }

                    } else {

                        currentPos++; // TODO implement Later
                    }

                } else if (currentPos + 1 <= lengthOfString) { //kombuwa and consonant at the end of a word

                    if (innerText.charAt(currentPos + 1) >= 3482 && innerText.charAt(currentPos + 1) <= 3526) { // kombuwa and consonant

                        innerText = swapCharacters(innerText, currentPos, currentPos + 1);
                        currentPos += 2;

                    } else {
                        currentPos++;
                    }

                } else {

                    currentPos++;
                }

            } else {

                currentPos++;
            }


        }

        return innerText;
    }

    public String applySpecialConsonantRules(String innerText) {

        int lengthOfString = innerText.length();
        char[] charSet = {3482, 3484, 3495, 3497, 3501, 3508, 3510};

        for (int currentPos = 0; currentPos < lengthOfString; ) {
            if (currentPos + 5 <= lengthOfString) { // string of 6 chars starting from a consonant
                if (containsChar(innerText.charAt(currentPos), charSet)) { // starting character is a consonant from the charSet
                    if (innerText.charAt(currentPos + 1) == 3546 && innerText.charAt(currentPos + 2) == 8205
                            && innerText.charAt(currentPos + 3) == 3515 && innerText.charAt(currentPos + 4) == 3535
                            && innerText.charAt(currentPos + 5) == 3530) { // Sinhala Char Kroo
                        innerText = swapCharacters(innerText, currentPos + 1, currentPos + 5);
                        innerText = replaceCharAt(innerText, currentPos + 4, 3549);
                        innerText = deleteCharAt(innerText, currentPos + 5);
                        lengthOfString = innerText.length();
                        currentPos = currentPos + 4;

                    } else {
                        currentPos++;
                    }
                } else {
                    currentPos++;
                }

            } else {
                currentPos++;
            }

        }
        return innerText;
    }

    private static String swapCharacters(String str, int i, int j) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(i, str.charAt(j));
        sb.setCharAt(j, str.charAt(i));
        return sb.toString();
    }

    private static String insertCharAt(String inputString, char inputChar, int charPosition) {
        StringBuilder sb = new StringBuilder(inputString);
        sb.insert(charPosition, inputChar);
        return sb.toString();

    }

    private static String deleteCharAt(String inputString, int charPosition) {

        StringBuilder sb = new StringBuilder(inputString);
        sb.deleteCharAt(charPosition);
        return sb.toString();

    }

    private static String replaceCharAt(String inputString, int charPosition, int inputChar) {

        return inputString.replace(Character.toString(inputString.charAt(charPosition)), Character.toString((char) inputChar));

    }

    private static boolean containsChar(char c, char[] array) {
        for (char x : array) {
            if (x == c) {
                return true;
            }
        }
        return false;
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
