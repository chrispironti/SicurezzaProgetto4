/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.math.BigInteger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
/**
 *
 * @author Daniele
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        //String nomeFile = "Da Fare.txt";
        KeyGenerator kg = KeyGenerator.getInstance("HmacSHA256");
        SecretKey key = kg.generateKey();
        String nomeFile = "documenti/timestamping.pdf";
        SecureDistributedStorage.distributeShares(nomeFile, 2, 5, key, "restoreInfo");
        ArrayList<BigInteger> servers = new ArrayList<>();
        servers.add(BigInteger.valueOf(1));
        servers.add(BigInteger.valueOf(3));
        servers.add(BigInteger.valueOf(5));
        List<BigInteger> fakes= SecureDistributedStorage.restoreFromShares("restoreInfo", servers, key);
        System.out.println("Numero di messaggi alterati: " + fakes.size());
    }
}
