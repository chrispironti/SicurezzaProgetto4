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
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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

    
    public static void distributeShares(String nomeFile, int k, int n, SecretKey key, String restoreInfoFile) throws Exception{
        SharesManager sm= new SharesManager(k, n);
        JSONObject j = sm.generateShares(nomeFile, key); 
        PrintWriter pw= new PrintWriter(restoreInfoFile);
        pw.println(j.toString());
        pw.close();
    }
    
    public static  List<BigInteger> restoreFromShares(String restoreInfoFile, List<BigInteger> restoreServers, SecretKey key) throws IOException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeyException, NotEnoughServersException{
        JSONObject restoreInfo= retrieveJSON(restoreInfoFile);
        int k= restoreInfo.getInt("RestoreNum");
        if(k<restoreServers.size()){
            throw new NotEnoughServersException();
        }
        BigInteger p = new BigInteger(Base64.getDecoder().decode(restoreInfo.getString("Prime")));
        SharesManager sm = new SharesManager(k, p);
        JSONArray macArray= restoreInfo.getJSONArray("MacList");
        List<byte[]> mac = new ArrayList<>();
        for(BigInteger b:restoreServers){
            mac.add(Base64.getDecoder().decode(macArray.getString(b.intValue())));
        }
        String fileName=restoreInfo.getString("FileName");
        int last = restoreInfo.getInt("LastBufferDim");
        return sm.reconstructFile(restoreServers, mac,fileName ,last , key);    
    }
    
    private static JSONObject retrieveJSON(String restoreInfoFile) throws IOException{
         ObjectInputStream ois=null;
        String s=null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(restoreInfoFile))); 
            s = (String) ois.readObject();
            ois.close();
      	}catch(ClassNotFoundException e){
            e.printStackTrace();
            System.exit(1);
        }
        finally{
            if(ois!=null){
                ois.close();
            }
        }
        return new JSONObject(s);
    }
    
    /*
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
*/
}
