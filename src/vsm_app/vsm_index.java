/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsm_app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Dell
 */
public class vsm_index {
    public Map<String, ArrayList<Integer>> indexx;
    public Map<String, ArrayList<Double>> tfidf;
    public Map<String, ArrayList<Integer>> qIndexx;
    public Map<String, ArrayList<Double>> qtfidf;
    public Map<Integer, ArrayList<Double>> documentVector;
    public ArrayList<Double> queryVector;
    public ArrayList<Double> relevance;
    public ArrayList<String> dName;
    public boolean inGUI=false;
//    public double docVectors[][];
    
    public String myQuery;
    VSM_App bm;
    public double myTime ;
    
    public vsm_index(VSM_App bm){
        this.bm=bm;
        bm.docTitles= new ArrayList<String>();
    }
    
    public void IndexFunction(){
        double startTime = System.nanoTime();
        indexx = new HashMap<String, ArrayList<Integer>>();
        tfidf = new HashMap<String, ArrayList<Double>>();       
        int docIdIterator=1;
        int titleCounter=0;
        for(int i=0;i<bm.fileContent.size();i++){
           
            String temp = bm.fileContent.get(i);
            if(titleCounter!=docIdIterator){
                titleCounter++;
                bm.docTitles.add(temp);
            }
            
            if(temp.equalsIgnoreCase("THE END"))
                docIdIterator++;
                
            
            String[] array = temp.split(" ");
            
            for(int j=0;j<array.length;j++){
                if(array[j].compareToIgnoreCase(" ")!=0 && array[j].compareToIgnoreCase("")!=0 && array[j].compareToIgnoreCase("THE")!=0 && array[j].compareToIgnoreCase("END")!=0){
                    if(this.indexx.containsKey(array[j])){
                        ArrayList<Integer> postingExist= indexx.get(array[j]);
//                        postingExist.add(docIdIterator);
                        int val=postingExist.get(docIdIterator-1);
                        postingExist.set(docIdIterator-1, val+1);
                        indexx.put(array[j],postingExist);
                    }
                    else{
                        ArrayList<Integer> postingNew= new ArrayList<Integer>();
                        
                        for(int x=0;x<bm.noOfDocuments;x++){
                            if(x!=docIdIterator-1)
                                postingNew.add(0);
                            else
                                postingNew.add(1);
                        }
                        indexx.put(array[j], postingNew);
//                        idf.put(array[j], postingNew);
                    }
                }
            }
        }
//        for(Map.Entry<String,ArrayList<Integer>>mEntry:indexx.entrySet()){
//            mEntry.setValue(mEntry.getValue());
//            Collections.sort(mEntry.getKey());
//        }        
        
        
        myTime = System.nanoTime()- startTime;
        myTime=myTime/1000000000.0;
    }
    
    public void searchQuery(String s) throws IOException{
            myQuery=s;
            writeOutput(s);        
            writeTFIDF();


        
        
    }
    
    public void writeTFIDF() throws IOException{
         File file = new File("outputTFIDF.txt");
        if ( !file.exists() ) 
            file.createNewFile();
        
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        if(inGUI==false){
        String headerr="\t\tTF-IDF\t\t\t";
        for(int i=0;i<this.bm.noOfDocuments;i++){
            String tmp=Integer.toString(i+1);
            headerr= headerr + tmp + "\t\t";
        }
        bw.write(headerr+"\n");
        }
        ArrayList<String> v = new ArrayList<String>(indexx.keySet());
//        Collections.sort(v);

//        for (Map.Entry<String, ArrayList<Integer>> entry : indexx.entrySet()) {
        if(inGUI==false){
        for (String str : v) {
            bw.write(str/*+entry.getValue().size()*/+"-->\t\t\t\t");
            ArrayList<Double> postingExist= tfidf.get(str);
            for(int i=0;i<postingExist.size();i++){
                if(postingExist.get(i)==0)
                    bw.write(postingExist.get(i)+ "\t->\t");
                else{
                    Double truncatedDouble = BigDecimal.valueOf(postingExist.get(i))
                        .setScale(3, RoundingMode.HALF_UP)
                        .doubleValue();                    
                    bw.write(truncatedDouble+ "\t->\t");
                }
            }
            bw.newLine();  
        }        
        }
        System.out.println("Query: "+myQuery);
        String arr[] = myQuery.split(" ");
        double qMagnitude=0;
        queryVector = new ArrayList();
        for (String str : v) {
//          for(int i=0;i<arr.length;i++){
            if(qIndexx.containsKey(str)){
//                System.out.println(qtfidf.get(arr[i]).get(0)+" ");
                queryVector.add(qtfidf.get(str).get(0));
                qMagnitude = qMagnitude + qtfidf.get(str).get(0);
            }
            else
                queryVector.add(0.0);
        }
        
//        for(int i=0;i<queryVector.size();i++){
//                System.out.println(queryVector.get(i)+" ");            
//        }
        
        documentVector = new HashMap<Integer, ArrayList<Double>>();       
        ArrayList<String> vv = new ArrayList<String>(tfidf.keySet());
        
        for (String str : vv) {
            ArrayList<Double> tmp= tfidf.get(str);
            for(int i=0;i<bm.noOfDocuments;i++){
                if(this.documentVector.containsKey(i+1)){
                    ArrayList<Double> postingExist= documentVector.get(i+1);
                    postingExist.add(tmp.get(i));
                    documentVector.put(i+1,postingExist);
                }
                else{
                    ArrayList<Double> postingNew= new ArrayList<Double>();
                    postingNew.add(tmp.get(i));
                    documentVector.put(i+1, postingNew);
//                        idf.put(array[j], postingNew);
                }                
            }
        }
        ArrayList<Double> docMagnitude= new ArrayList<Double>();
        
        for (Map.Entry<Integer, ArrayList<Double>> entry : documentVector.entrySet()) {
            double summ=0.0;
            for(int i=0;i<entry.getValue().size();i++)
                summ = summ + entry.getValue().get(i);
            docMagnitude.add(summ);
//            bw.newLine();  
        }
        
        ArrayList<Integer> vvv= new ArrayList<Integer>(documentVector.keySet());
        relevance = new ArrayList<Double>();
        
        for (int i=0 ; i<vvv.size();i++){
            ArrayList<Double> postingExist= documentVector.get(i+1);
            double sum=0;
            for(int j=0;j<postingExist.size();j++){
                sum = sum + (postingExist.get(j) * queryVector.get(j));
            }
            double tmp= sum/ (Math.sqrt(docMagnitude.get(i)*docMagnitude.get(i)) * Math.sqrt(qMagnitude*qMagnitude)  );
            relevance.add(tmp);
        }

//        relevance.sort(null);
        ArrayList<Double> temp = relevance;
        dName= new ArrayList();
        dName = (ArrayList<String>)bm.docTitles.clone();
        
        for(int i=1; i<temp.size(); i++) {
            boolean is_sorted = true;

            for(int j=0; j < temp.size() - i; j++) { // skip the already sorted largest elements
              if(temp.get(j) < temp.get(j+1)) {
                 double t = temp.get(j);
                 temp.set(j,temp.get(j+1));
                 temp.set(j+1,t);
                 String tt=dName.get(j);
                 dName.set(j, dName.get(j+1));
                 dName.set(j+1, tt);
                 is_sorted = false;
              }
            }

            if(is_sorted) return;
          }
        
        
//        System.out.println("DName\t\t\tRelevance");
//        for(int i=0;i<temp.size();i++)
//            System.out.println(dName.get(i)+"\t"+temp.get(i)*1000);
        
        
        bw.flush();
        bw.close();        
    }
        
     public void writeDocVector ( ) throws IOException {
        File file = new File("outputDocVector.txt");
        
        if ( !file.exists() ) 
            file.createNewFile();
        
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
       
        for (Map.Entry<Integer, ArrayList<Double>> entry : documentVector.entrySet()) {
            bw.write(entry.getKey()+" --> ");
            for(int i=0;i<entry.getValue().size();i++){
                bw.write(entry.getValue().get(i)+ " -> ");
                if(entry.getValue().get(i)==0.0)
                    bw.write(entry.getValue().get(i)+ "\t->\t");
                else{
                    Double truncatedDouble = BigDecimal.valueOf(entry.getValue().get(i))
                        .setScale(3, RoundingMode.HALF_UP)
                        .doubleValue();                    
                    bw.write(truncatedDouble+ "\t->\t");
                }
            }
            bw.newLine();  
        }
        
        
        
        bw.flush();
        bw.close();
    }                    
        
    
     public void writeOutput (String s) throws IOException {

        
        qIndexx = new HashMap<String, ArrayList<Integer>>();
        qtfidf = new HashMap<String, ArrayList<Double>>();
        
        String arr[]= s.split(" ");
        for(int i=0;i<arr.length;i++){
            if(qIndexx.containsKey(arr[i])){
                ArrayList<Integer> postingExist= qIndexx.get(arr[i]);
                int val=postingExist.get(0);
                postingExist.set(0, val+1);
                qIndexx.put(arr[i],postingExist);                
            }
            else{
                ArrayList<Integer> postingNew= new ArrayList<Integer>();
                postingNew.add(1);
                qIndexx.put(arr[i], postingNew);
            }
        }
        
        
         File file = new File("outputIndex.txt");
        
        if ( !file.exists() ) 
            file.createNewFile();
        
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        if(inGUI==false){
        String headerr="\t\t\t\t\t\t";
        for(int i=0;i<this.bm.noOfDocuments;i++){
            String tmp=Integer.toString(i+1);
            headerr= headerr + tmp + "\t\t";
        }
        bw.write(headerr+"\n");
        }
        ArrayList<String> v = new ArrayList<String>(indexx.keySet());
//        Collections.sort(v);

//        for (Map.Entry<String, ArrayList<Integer>> entry : indexx.entrySet()) {
        for (String str : v) {
            if(inGUI==false)
                bw.write(str/*+entry.getValue().size()*/+"-->\t\t\t\t");
            ArrayList<Integer> postingExist= indexx.get(str);
            ArrayList<Double> postingNew= new ArrayList<Double>();
            int df=0;
            int countt=0;
            for(int i=0;i<postingExist.size();i++){
                int tf = postingExist.get(i);
                if(inGUI==false)
                    bw.write(tf+ "\t->\t");
                countt=countt+tf;
                if(tf!=0)
                    df = df + 1;
            }
            if(inGUI==false){
                bw.write(countt+ "<-COUNT");
                bw.newLine();  
            }
            double idff = Math.log10((double)bm.noOfDocuments/(double)df);
            for(int i=0;i<postingExist.size();i++){
                double tff = postingExist.get(i);
                tff = tff * idff;
                postingNew.add(tff);
            }            
            tfidf.put(str, postingNew);
            if(qIndexx.containsKey(str)){
                ArrayList<Integer> qPosting= qIndexx.get(str);
                double Qtff= (double) qPosting.get(0);
                Qtff = Qtff * idff;
                ArrayList<Double> qNew= new ArrayList<Double>();
                qNew.add(Qtff);
                qtfidf.put(str, qNew);
            }
        }        
        
        if(inGUI==false){
            for (Map.Entry<String, ArrayList<Integer>> entry : indexx.entrySet()) {
                bw.write(entry.getKey()/*+entry.getValue().size()*/+"-->\t\t\t\t");
                for(int i=0;i<entry.getValue().size();i++)
                    bw.write(entry.getValue().get(i)+ "\t->\t");
                bw.newLine();  
            }
        }
        
        
        bw.flush();
        bw.close();
    }           
}
