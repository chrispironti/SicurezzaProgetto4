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
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.json.*;


/**
 *
 * @author gennaroavitabile
 */
public class SecureDistributedStorage{

    private String nomeFile;
    //Inormazione per recuperare il file nei server?
    private int k;
    private int n;

    public SecureDistributedStorage(String nomeFile, int k, int n) {
        
        this.nomeFile = nomeFile;
        this.k = k;
        this.n = n;
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
        macObj = new Mac(new MacSpi(),)
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
        
        }
    }
}
