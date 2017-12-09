/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.json.*;

/**
 *
 * @author gennaroavitabile
 */
public class SecureDistributedStorage implements Serializable{

    private String nomeFile;
    //Inormazione per recuperare il file nei server?
    private byte[] mac; //Non sono sicuro debba essere una stringa
    private List<String> servers;
    private String client;
    private int k;
    private int n;
    private int modLength;

    public SecureDistributedStorage(String nomeFile, List<String> servers, String client, int k, int modLength) {
        this.nomeFile = nomeFile;
        this.servers = servers;
        this.client = client;
        this.k = k;
        this.n = servers.size();
        this.modLength = modLength;
    }
    
    
    public SecureDistributedStorage(String nomeFileRipristino){
        
    }
    
    public void distributeShares() throws FileNotFoundException, IOException{
        SecretSharing s = new SecretSharing(k, n, modLength);
        int currentSecret=0;
        BufferedInputStream is= new BufferedInputStream(new FileInputStream(nomeFile));
        
        //Salvataggio delle info per il Client
        JSONObject j = new JSONObject();
        j.put("nomeFile", nomeFile);
        j.put("numServer", k);
        j.put("macKey", "macKey");
        j.put("Mac", "Mac");
        
        //Scrittura info file del Client
        String clientInfoFile = "C:\\Users\\Christopher\\Documents\\NetBeansProjects\\SicurezzaProgetto4\\Filesystem\\"+client+"\\clientInfo.txt";
        String jout = j.toString();
        PrintWriter pw = new PrintWriter(clientInfoFile);
        pw.println(jout);
        pw.close();
        
        HashMap<BigInteger,LinkedList<BigInteger>> shares = new HashMap<>();
        
        for(int i = 1; i <= n; i++){
            shares.put(BigInteger.valueOf(i), new LinkedList<>());
        }
        
        //Generazione chiave Mac
        KeyGenerator keyGenerator = null;
        try {
                keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                System.exit(1);
        }
        
        keyGenerator.init(256, new SecureRandom());
	SecretKey simmKey = keyGenerator.generateKey();
        
        Mac macObj = null;
        try {
            macObj = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Algoritmo non supportato");
        }
        
        try {
            macObj.init(simmKey);
        } catch (InvalidKeyException ex) {
            System.out.println("Invalid Key");
        }
        mac = macObj.doFinal();
        
        
//        HashMap<BigInteger,BigInteger> temp = null;
//        byte[] buffer = new byte[32];
//        int r;
//        while((r = is.read(buffer, 0, buffer.length))!=-1){
//            if(r < 32){
//                byte[] newbuffer = Arrays.copyOfRange(buffer, 0, r);
//                temp = s.split(newbuffer);
//            }else
//                temp = s.split(buffer);
//             for(Map.Entry<BigInteger, BigInteger> e: temp.entrySet()){
//                 shares.get(e.getKey()).add(e.getValue());
//             }
//        }
//        is.close();
        
        
        
        while(currentSecret!=-1){
            currentSecret=is.read();
            //write negli stream se il valore non è -1
        }
    }
    
    private class SharesManager {
        private final int bufferSize = 32;
        private final int modLength = 256;
        private SecretSharing s;
    
        public SharesManager(int k, int n){
            this.s = new SecretSharing(k, n, this.modLength);
        }

        public void generateShares(String fileToSplit) throws IOException, Exception{
            //Inizializzazione stream
            BufferedInputStream is = null;
            ArrayList<BufferedOutputStream> outList = new ArrayList<>();
            for(int i = 0; i < n; i++){
                outList.add(null);
            }
            try{
                for(int i = 0; i < n; i++){
                    outList.add(new BufferedOutputStream(new FileOutputStream((i+1)+"/"+fileToSplit)));
                }
                is= new BufferedInputStream(new FileInputStream(fileToSplit));
                //Generazione e scrittura shares
                HashMap<BigInteger,BigInteger> temp = null;
                byte[] buffer = new byte[this.bufferSize];
                int r;
                while((r = is.read(buffer, 0, buffer.length))!=-1){
                    if(r < this.bufferSize){
                        //Per ultimo byte letto
                        byte[] newbuffer = Arrays.copyOfRange(buffer, 0, r);
                        temp = s.split(newbuffer);
                    }else
                        temp = s.split(buffer);
                     for(int i = 1; i<=n; i++){
                        //Aggiunta padding e scrittura
                        byte[] padded = BigIntegerToByteArray(temp.get(BigInteger.valueOf(i)),this.bufferSize);
                        outList.get(i-1).write(padded);
                     }
                }
            }finally{
                if(is!=null)
                    is.close();
                for(int i = 0; i < n; i++){
                    BufferedOutputStream out = outList.get(i);
                    if(out!=null)
                        out.close();
                }
            }
        }
        
        public void reconstructFile(ArrayList<BigInteger> servers, String fileToCombine, int lastShareLength) throws FileNotFoundException, IOException{
            ArrayList<BufferedInputStream> inList = new ArrayList<>();
            for(BigInteger b: servers){
                inList.add(null);
            }
            BufferedOutputStream out = null;
            try{
                out = new BufferedOutputStream(new FileOutputStream(fileToCombine+".ricostruito"));
                for(BigInteger b: servers){
                    inList.add(new BufferedInputStream(new FileInputStream((b.intValue())+"/"+fileToCombine)));
                }
                long fileLength = new File(servers.get(0).intValue()+"/"+fileToCombine).length();
                HashMap<BigInteger,BigInteger> temp = new HashMap<>();
                byte[] buffer = new byte[this.bufferSize];
                long r = 0;
                while(r < fileLength-this.bufferSize){
                    for(int i = 0; i< servers.size(); i++){
                        inList.get(i).read(buffer, 0, buffer.length);
                        temp.put(servers.get(i), new BigInteger(1,buffer));
                    }
                    r+=this.bufferSize;
                    BigInteger result = s.combine(temp);
                    out.write(BigIntegerToByteArray(result, this.bufferSize));
                }
                //Lettura ultimo byte di ciascun file
                for(int i = 0; i< servers.size(); i++){
                    inList.get(i).read(buffer, 0, buffer.length);
                    temp.put(servers.get(i), new BigInteger(1,buffer));
                }
                BigInteger result = s.combine(temp);
                out.write(BigIntegerToByteArray(result, lastShareLength));
            }finally{
                if(out!=null)
                    out.close();
                for(int i = 0; i < servers.size(); i++){
                    BufferedInputStream bis = inList.get(i);
                    if(bis!=null)
                        bis.close();
                }
            }
        }
        
        private byte[] BigIntegerToByteArray(BigInteger bi, int size){
            byte[] toEval = bi.toByteArray();
            byte[] padded = new byte[size];
            if(toEval.length > size)
               toEval = Arrays.copyOfRange(toEval, 1, padded.length);
            if(toEval.length < size){
               for(int i = padded.length - toEval.length; i < padded.length ; i++)
                   padded[i] = toEval[i];
               toEval = padded;
            }
            return toEval;
        }
    }
}
