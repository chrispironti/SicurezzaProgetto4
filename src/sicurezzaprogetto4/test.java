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
 * DA fare/decidere ancora:
 * -Keyring: Lo usiamo per le chiavi MAC ? Quindi usiamo un sistema di username e password ?
 * -Generazione nomi file su server: Dall'hash non si può risalire a nessuna informazione.
 *  Casomai però cancellassimo il file sul server e poi lo ricaricassimo sullo stesso,
 *  quello avrebbe lo stesso nome. E' un problema ?
 * -Nomi server: Manteniamo i numeri o mettiamo dei nomi reali ? 
 * -Scrivere main di test
 * -Metodo available di BufferInputStream va bene ?
 * -Costruttore di Secret Sharing chiamato da Shares Manager con n = 0. A che serve ?
 * -Non è che è un po' inutile MacInputStream ? Alla fine è solo per atteggiarci xD
 * -Scrivere la doc ovviamente
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
        SecureDistributedStorage.distributeShares(nomeFile, 3, 5, key, "clients/restoreInfo");
        //new File(nomeFile).delete();
        ArrayList<BigInteger> servers = new ArrayList<>();
        servers.add(BigInteger.valueOf(1));
        servers.add(BigInteger.valueOf(3));
        servers.add(BigInteger.valueOf(5));
        //servers.add(BigInteger.valueOf(4));
        List<BigInteger> fakes= SecureDistributedStorage.restoreFromShares("restoreInfo", servers, key);
        System.out.println("Numero di messaggi alterati: " + fakes.size());
    }
}
