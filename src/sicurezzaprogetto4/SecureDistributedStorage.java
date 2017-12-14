/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.json.*;


/**
 *
 * @author gennaroavitabile
 */
public class SecureDistributedStorage{

    
    public static void distributeShares(String nomeFile, int k, int n, String keychainFile, char[] password, String restoreInfoFile) throws Exception{
        Keychain userKeyChain = new Keychain(keychainFile, password);
        SecretKey key = userKeyChain.getSecretKey("Key/HmacSHA256/Main");
        SharesManager sm = new SharesManager(k, n);
        JSONObject j = sm.generateShares(nomeFile, key); 
        PrintWriter pw= new PrintWriter(restoreInfoFile+".info");
        pw.println(j.toString());
        pw.close();
    }
    
    public static  ArrayList<BigInteger> restoreFromShares(String restoreInfoFile, ArrayList<BigInteger> restoreServers, String keychainFile, char[] password) throws IOException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeyException, NotEnoughServersException{
        Keychain userKeyChain = new Keychain(keychainFile, password);
        SecretKey key = userKeyChain.getSecretKey("Key/HmacSHA256/Main");
        JSONObject restoreInfo= retrieveJSON(restoreInfoFile);
        int k= restoreInfo.getInt("RestoreNum");
        //Commentare se si è in fase di testing. Decommentare in caso contrario.
        /*if(restoreServers.size()<k){
            throw new NotEnoughServersException();
        }*/
        BigInteger p = new BigInteger(Base64.getDecoder().decode(restoreInfo.getString("Prime")));
        SharesManager sm = new SharesManager(k, p);
        JSONArray macArray= restoreInfo.getJSONArray("MacList");
        ArrayList<byte[]> mac = new ArrayList<>();
        for(BigInteger b:restoreServers){
            mac.add(Base64.getDecoder().decode(macArray.getString((b.intValue())-1)));
        }
        String fileName=restoreInfo.getString("FileName");
        int last = restoreInfo.getInt("LastBufferDim");
        byte[] random = Base64.getDecoder().decode(restoreInfo.getString("Random"));
        return sm.reconstructFile(restoreServers, mac,fileName ,last , key, random);    
    }
    
    private static JSONObject retrieveJSON(String restoreInfoFile) throws IOException{
        byte[] encoded = Files.readAllBytes(Paths.get(restoreInfoFile));
        return new JSONObject(new String(encoded, "UTF8"));
    }
}
