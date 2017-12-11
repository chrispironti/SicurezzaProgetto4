package sicurezzaprogetto4;

import java.math.BigInteger;
import java.util.Arrays;

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
        if(toEval.length > size)
           toEval = Arrays.copyOfRange(toEval, 1, padded.length+1);
        if(toEval.length < size){
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
}
