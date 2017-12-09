/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        //String nomeFile = "Da Fare.txt";
        String nomeFile = "timestamping.pdf";
        SharesManager sm = new SharesManager(2,5);
        int last = sm.generateShares(nomeFile);
        ArrayList<BigInteger> servers = new ArrayList<>();
        servers.add(BigInteger.valueOf(1));
        servers.add(BigInteger.valueOf(3));
        servers.add(BigInteger.valueOf(5));
        sm.reconstructFile(servers, nomeFile, last);
    }
}
