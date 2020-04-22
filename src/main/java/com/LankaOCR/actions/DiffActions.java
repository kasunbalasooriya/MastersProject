/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.LankaOCR.actions;
import java.util.LinkedList;
import com.LankaOCR.actions.diff_match_patch.Operation;


/**
 *
 * @author kasun
 */
public class DiffActions {
    
 protected void createDiffReport(String orignalText, String ocrText){
 
     //TODO implement method to diff the original vs output
     
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<diff_match_patch.Diff> diff = dmp.diff_main("ට්රමප් පරිපාලනය මෙන්ම රිපබ්ලිකන් තියෝජිතයන්ද වෛරස", "ට‍්‍රම්ප් පරිපාලනය මෙන්ම රිපබ්ලිකන් නියෝජිතයන්ද වෛරස");
    // Result: [(-1, "Hell"), (1, "G"), (0, "o"), (1, "odbye"), (0, " World.")]
    dmp.diff_cleanupSemantic(diff);
    // Result: [(-1, "Hello"), (1, "Goodbye"), (0, " World.")]
    System.out.println(diff);

    for (int i =0; i<diff.size();i++){

        if (diff.get(i).operation== Operation.EQUAL){
        System.out.println(diff.get(i));
        }
    }
 
 } 
    
}
