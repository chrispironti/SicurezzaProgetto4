/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Daniele
 */
public class TestSecretSharing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws TooBigSecretException, UnsupportedEncodingException, NotEnoughServersException {
        //Test delle sole funzioni split e combine in Secret Sharing:
        //Con k ed n molto elevati
        //Shamir(8,12)
        System.out.println("\n**************TEST FUNZIONI SECRETSHARING SHAMIR(8,12)****************");
        SecretSharing s = new SecretSharing(8, 12, 32*8);
        byte[] secret = "SicurezzaInformatica2017".getBytes("UTF8");
        HashMap<BigInteger,BigInteger> shares = s.split(secret);
        HashMap<BigInteger,BigInteger> toTest = new HashMap<>();
        Iterator it = shares.entrySet().iterator();
        System.out.println("Test Shamir(8,12). Tentativo di ricostruzione con 5 shares:");
        for(int i = 0; i < 5; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        System.out.println("Risultato:" + new String(s.combine(toTest).toByteArray()));
        
        toTest.clear();
        Iterator it2 = shares.entrySet().iterator();
        System.out.println("\nTest Shamir(8,12). Tentativo di ricostruzione con 8 shares: ");
        for(int i = 0; i < 8; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it2.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        
        System.out.println("Risultato: " + new String(s.combine(toTest).toByteArray()) + "\n");
        
        //Shamir(30,50)
        System.out.println("**************TEST FUNZIONI SECRETSHARING SHAMIR(30,50)****************");
        SecretSharing s2 = new SecretSharing(30, 50, 32*8);
        byte[] secret2 = "SicurezzaInformatica2017".getBytes("UTF8");
        HashMap<BigInteger,BigInteger> shares2 = s2.split(secret);
        toTest.clear();
        Iterator it3 = shares2.entrySet().iterator();
        System.out.println("Test Shamir(30,50). Tentativo di ricostruzione con 18 shares:");
        for(int i = 0; i < 18; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it3.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        System.out.println("Risultato:" + new String(s2.combine(toTest).toByteArray()));
        
        toTest.clear();
        Iterator it4 = shares2.entrySet().iterator();
        System.out.println("\nTest Shamir(30,50). Tentativo di ricostruzione con 36 shares: ");
        for(int i = 0; i < 36; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it4.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        
        System.out.println("Risultato: " + new String(s2.combine(toTest).toByteArray())+ "\n");
        System.out.println("***************FINE TEST******************");
    }
    
}
