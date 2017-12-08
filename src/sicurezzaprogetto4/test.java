/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;
import java.util.*;
import java.math.BigInteger;
/**
 *
 * @author Daniele
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        byte[] test = new byte[32];
        new Random().nextBytes(test);
        BigInteger b = new BigInteger(test);
        byte[] converted = b.toByteArray();
        if(new String(test).compareTo(new String(converted)) == 0){
            System.out.println("True");
        }else
            System.out.println("False");
        
    }
    
}
