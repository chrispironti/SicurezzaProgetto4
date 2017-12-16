package sicurezzaprogetto4;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniele
 */
public class SSUtils {
    
    public static byte[] BigIntegerToByteArray(BigInteger bi, int size){
        byte[] toEval = bi.toByteArray();
        byte[] padded = new byte[size];
        if(toEval.length > size) //Rimozione padding in toEval fino a eguagliare size
           toEval = Arrays.copyOfRange(toEval, 1, padded.length+1);
        if(toEval.length < size){ //Aggiunta padding da toEval fino a eguagliare size
           int i = padded.length-toEval.length;
           for(int j = 0; j < toEval.length; j++){
               padded[i] = toEval[j];
               i += 1;
           }
           toEval = padded;
        }
        return toEval;
    }
    
    public static byte[] arrayConcat(byte[] array1, byte[] array2){
       byte[] array1and2 = new byte[array1.length + array2.length];
       System.arraycopy(array1, 0, array1and2, 0, array1.length);
       System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
       return array1and2;
   }
    
    public static boolean byteArrayEquals(byte[] array1, byte[] array2){
        if(array1.length!=array2.length){
            return false;
        }
        boolean result=true;
        for(int i=0;i<array1.length && result;i++){
            if(array1[i]!=array2[i]){
                result=false;
            }
        }
        return result;
    }
    
    public static String generateFileName(String fileName, int serverID, byte[] random) throws NoSuchAlgorithmException{
        //Nome file da salvare sui server come H(randomSeed|IDServer|nomefileoriginario).
        //Tutti questi campi non sono e non devono essere noti ai server.
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] concat = SSUtils.arrayConcat(random, (""+serverID).getBytes());
        fileName = Base64.getEncoder().withoutPadding().encodeToString(md.digest(SSUtils.arrayConcat(concat, fileName.getBytes())));
        fileName = fileName.replaceAll("/", "");
        return fileName;
    }
}
