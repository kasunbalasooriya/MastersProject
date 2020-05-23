/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LankaOCR.actions;

import com.LankaOCR.actions.diff_match_patch.Operation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;



/**
 *
 * @author kasun
 */
public class DiffActions {
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    public void createDiffReport(String orignalText, String ocrText, String reportOutputPath) { // method to create diff report

        diff_match_patch dmp = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(orignalText, ocrText,true);
//        dmp.diff_cleanupSemantic(diff);
        StringBuilder equalWords = new StringBuilder();
        

        for (int i = 0; i < diff.size(); i++) {

            if (diff.get(i).operation == Operation.EQUAL) {
                equalWords.append(diff.get(i));
                equalWords.append("\n");
            }
        }
        
  
       try (OutputStreamWriter diffWriter = new OutputStreamWriter(new FileOutputStream(reportOutputPath), StandardCharsets.UTF_8)) {
                diffWriter.write(equalWords.toString());
            } catch (IOException ex) {
            log.error(ex);
        }
            
        
    }
    
        
         

}
