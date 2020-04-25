/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lankaocr.actions;

import java.util.LinkedList;
import com.lankaocr.actions.diff_match_patch.Operation;


/**
 *
 * @author kasun
 */
public class DiffActions {

    protected void createDiffReport(String orignalText, String ocrText, String reportOutputPath) { // method to create diff report

        diff_match_patch dmp = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(orignalText, ocrText);
        dmp.diff_cleanupSemantic(diff);
//        StringBuilder out
        

        for (int i = 0; i < diff.size(); i++) {

            if (diff.get(i).operation == Operation.EQUAL) {
                System.out.println(diff.get(i));
            }
        }

    }

}
