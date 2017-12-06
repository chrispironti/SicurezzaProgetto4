/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.math.BigInteger;
/**
 *
 * @author Daniele
 */
public class SicurezzaProgetto4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        // TODO code application logic here
        
        SecretSharing s = new SecretSharing(3,5);
        byte[] secret = "SicurezzaInformatica2017".getBytes("UTF8");
        HashMap<BigInteger,BigInteger> shares = s.split(secret);
        HashMap<BigInteger,BigInteger> toTest = new HashMap<>();
        Iterator it = shares.entrySet().iterator();
        System.out.println("Test Shamir(3,5). Tentativo di ricostruzione con due (1,2) shares:");
        for(int i = 0; i < 2; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        System.out.println("Risultato:" + new String(s.combine(toTest),"UTF8"));
        System.out.println("\nTest Shamir(3,5). Tentativo di ricostruzione con tre (3,4,5) shares: ");
        for(int i = 0; i < 3; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        System.out.println("Risultato: " + new String(s.combine(toTest),"UTF8"));
    }
    
}
