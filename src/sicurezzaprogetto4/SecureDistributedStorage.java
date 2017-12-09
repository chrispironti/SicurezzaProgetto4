/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

        public HashMap<BigInteger, String> generateShares(String nomeFile, HashMap<String,String> servers) throws IOException, Exception{
            BufferedInputStream is = null;
            HashMap<BigInteger,LinkedList<BigInteger>> shares = null;
            try{
                is= new BufferedInputStream(new FileInputStream(nomeFile));
                //Mapping tra identità server e nomi server
                HashMap<BigInteger,String> mapping = new HashMap<>();
                int i = 1;
                Iterator<String> it = servers.keySet().iterator();
                while(it.hasNext()){
                    mapping.put(BigInteger.valueOf(i), it.next());
                }
                //Generazione e scrittura shares
                HashMap<BigInteger,BigInteger> temp = null;
                byte[] buffer = new byte[this.bufferSize];
                int r;
                while((r = is.read(buffer, 0, buffer.length))!=-1){
                    if(r < 32){
                        byte[] newbuffer = Arrays.copyOfRange(buffer, 0, r);
                        temp = s.split(newbuffer);
                    }else
                        temp = s.split(buffer);
                     for(Map.Entry<BigInteger, BigInteger> e: temp.entrySet()){
                         shares.get(e.getKey()).add(e.getValue());
                     }
                }
            }finally{
                is.close();
            }
        }
    }
}
