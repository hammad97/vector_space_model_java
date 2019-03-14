/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsm_app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Dell
 */
public class VSM_App {
    ArrayList<String> fileContent;
    ArrayList<String> stopWords;
    ArrayList<String> docTitles;
    int noOfDocuments=0;
    public double myTime;
    public void readFile ( ) throws FileNotFoundException {
        fileContent = new ArrayList<String>();
        fileContent = fetchFromFile(new File("WS Complete Works.txt"));
        
        
    }
    
    private ArrayList<String> fetchFromFile ( File file ) throws FileNotFoundException {
        double startTime = System.nanoTime();
        Scanner in = new Scanner(file);
        updateStopWord();
        String temp;
        boolean ignoreText=false; 
        ArrayList<String> fileContent = new ArrayList<String>();
        while ( in.hasNext() ) {
            temp = in.nextLine();
            if(isInt(temp)){             
                while(in.hasNext()){
                    temp = in.nextLine();
                    if(temp.equalsIgnoreCase("THE END")){
                        noOfDocuments++;
                        fileContent.add(temp);
                        break;
                    }
                    if(temp.length()>0){
                        if(!isInt(temp)){
                            if(temp.contains("<<"))
                                ignoreText=true;
                            if(temp.contains(">>"))
                                ignoreText=false;
                            
                           if(ignoreText==false){
                               temp=temp.replaceAll("[^a-zA-Z ]","");
                               temp=temp.toLowerCase();
                               String noStopWords[]=temp.split(" ");
                               String finalTemp="";
                               for(int i=0;i<noStopWords.length;i++){
                                   if(!stopWords.contains(noStopWords[i]))
                                       finalTemp=finalTemp.concat(noStopWords[i]+" ");
                               }
                               fileContent.add(finalTemp);
                           }
                        }
                    }
                }
                
            }
            
        }
        
        myTime = System.nanoTime()- startTime;
        myTime=myTime/1000000000.0;
        return fileContent;
    }
    public static boolean isInt(String str) {
    try { 
        Integer.parseInt(str); 
    } catch(Exception e) {
        return false;
    }
    return true;
}
    public void updateStopWord ( ) throws FileNotFoundException {
        File file = new File("Stopword-List.txt");
        Scanner in = new Scanner(file);
        
        stopWords = new ArrayList<String>();
        while ( in.hasNext() ){ 
            stopWords.add(in.nextLine());
        }
    }    
    public void writeOutput ( ) throws IOException {
        File file = new File("output.txt");
        
        if ( !file.exists() ) 
            file.createNewFile();
        
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        
        for (int i=0;i<fileContent.size();i++ ) {
                bw.write(fileContent.get(i));
                bw.newLine();
            }
        
        
        bw.flush();
        bw.close();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
//            Scanner in= new Scanner(System.in);
//            String qu= in.next();
            VSM_App vsm = new VSM_App();
            vsm.readFile();
//            vsm.writeOutput();
            System.out.println(vsm.noOfDocuments);
            
            vsm_index vIndex = new vsm_index(vsm);
//            vIndex.searchQuery("sonnets by william shakespeare creatures");
            vIndex.searchQuery("sonnets william shakespeare creatures fairest");
            vIndex.writeDocVector();
            
    }
    
}
