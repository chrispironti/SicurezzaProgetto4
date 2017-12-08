/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;
import java.io.*;
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
    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        // TODO code application logic here
        /*
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
        
        toTest.clear();
        System.out.println("\nTest Shamir(3,5). Tentativo di ricostruzione con tre (3,4,5) shares: ");
        for(int i = 0; i < 3; i++){
            Map.Entry<BigInteger,BigInteger> elem = (Map.Entry<BigInteger, BigInteger>)it.next();
            toTest.put(elem.getKey(), elem.getValue());
        }
        System.out.println("Risultato: " + new String(s.combine(toTest),"UTF8"));*/
        SecretSharing s = new SecretSharing(3,5);
        String nomeFile = "Da Fare.txt";
        BufferedInputStream is= new BufferedInputStream(new FileInputStream(nomeFile));
        HashMap<BigInteger,LinkedList<BigInteger>> shares = new HashMap<>();
        for(int i = 1; i <= s.n; i++){
            shares.put(BigInteger.valueOf(i), new LinkedList<>());
        }
        HashMap<BigInteger,BigInteger> temp = null;
        byte[] buffer = new byte[16];
        while(is.read(buffer)!=-1){
            temp = s.split(buffer);
             for(Map.Entry<BigInteger, BigInteger> e: temp.entrySet()){
                 shares.get(e.getKey()).add(e.getValue());
             }
        }
        is.close();
        HashMap<BigInteger,LinkedList<BigInteger>> toVerify = new HashMap<>();
        toVerify.put(BigInteger.valueOf(1), shares.get(BigInteger.valueOf(1)));
        toVerify.put(BigInteger.valueOf(3), shares.get(BigInteger.valueOf(3)));
        toVerify.put(BigInteger.valueOf(4), shares.get(BigInteger.valueOf(4)));
        BufferedOutputStream out= new BufferedOutputStream(new FileOutputStream(nomeFile+".ricostruito"));
        HashMap<BigInteger,BigInteger> toSend = new HashMap<>();
        while(!toVerify.get(BigInteger.valueOf(1)).isEmpty()){
           for(Map.Entry<BigInteger, LinkedList<BigInteger>> e: toVerify.entrySet()){
               toSend.put(e.getKey(), e.getValue().remove());
           }
           out.write(s.combine(toSend));
        }
        out.close();
    }    
}
