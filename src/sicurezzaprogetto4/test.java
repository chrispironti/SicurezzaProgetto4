/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;
import java.io.File;
import java.util.*;
import java.math.BigInteger;
import java.security.Key;
import javax.crypto.KeyGenerator;

/**
 * Test provare 2,5 e 3,5, per ognuno:
 * Generare le share, Cancello il file originario, Ricombino
 * Provo a ricombinare con chiave Mac sbagliata
 * Provo a ricombinare con più di k shares
 * Provo a ricombinare con meno di k shares (vedere eccezione)
 * Alterare uno dei campi del file 
 */
public class test {

    /**
    * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        int usersNumber = 5;
        //Generazione keyring di client random
        System.out.println("**************INIZIO TEST****************");
        System.out.println("Generazione Keyring di "+ usersNumber +" client random...");
        KeyGenerator kg = KeyGenerator.getInstance("HmacSHA256");
        //NOTA: Generiamo un Keyring in più che ci sarà utile per alcuni test dopo
        for(int i = 1; i <= usersNumber+1; i++){
            KeychainUtils.generateEmptyKeychain(("client"+i).toCharArray(), "keyring/Client"+ i +".kc");
            HashMap<String,Key> userKey = new HashMap<>();
            userKey.put("Key/HmacSHA256/Main", kg.generateKey());
            KeychainUtils.addKeysInKeychain("keyring/Client"+ i +".kc", userKey, ("client"+i).toCharArray());
        }
        
        ArrayList<String> extensions = new ArrayList<>();
        extensions.add(0, "");
        extensions.add(1, ".mp3");
        extensions.add(2, ".pdf");
        extensions.add(3, ".png");
        extensions.add(4, ".xls");
        extensions.add(5, ".txt");
        
        //Test Shamir(2,5)
        System.out.println("**************TEST SHAMIR(2,5)****************");
        System.out.println("Caricamento files da splittare...");
        //Test split + combine
        for(int i = 1; i <= usersNumber; i++){
            System.out.println("**********NUOVO DOCUMENTO*************");
            SecureDistributedStorage.distributeShares("documenti/2_5/documento"+i+extensions.get(i), 2, 5, 
                    "keyring/Client"+ i +".kc", ("client"+i).toCharArray(), "clients/client"+i+"documento"+i+"2_5");
            System.out.println("Split Documento "+i+" OK !");
            System.out.println("Cancellazione documento originale...");
            new File("documenti/2_5/documento"+i+extensions.get(i)).delete();
            
            //Test combine per ogni valore di k
            for(int k = 1; k <= 5; k++){
                System.out.println("*******COMBINE CON K = "+k+"************");
                if(k < 2)
                    System.out.println("NOTA: Il combine viene eseguito lo stesso. Controllare poi il file nella cartella per verificare che esso non è correttamente ricostruito.");
                ArrayList<BigInteger> servers = new ArrayList<>();
                for(int h = 1; h <= k; h++)
                    servers.add(BigInteger.valueOf(h));
                SecureDistributedStorage.restoreFromShares("clients/client"+i+"documento"+i+"2_5.info",servers, "keyring/Client"+ i +".kc", ("client"+i).toCharArray());
                new File("ricostruiti/documento"+i+extensions.get(i)).renameTo(new File("ricostruiti/documento"+i+"with_k_"+k+"2_5"+extensions.get(i)));
                System.out.println("OK !");
                
                //Test shares alterate e conseguente fallimento MAC: si simula da programma passando la chiave MAC di un altro utente
                System.out.println("Test shares alterate");
                List<BigInteger> fakes = SecureDistributedStorage.restoreFromShares("clients/client"+i+"documento"+i+"2_5.info",servers, "keyring/Client"+ (i+1) +".kc", ("client"+(i+1)).toCharArray());
                System.out.println("Numero di messaggi alterati: " + fakes.size());
                }   
        }
        
        //Test Shamir(3,5)
        System.out.println("**************TEST SHAMIR(3,5)****************");
        System.out.println("Caricamento files da splittare...");
        
        //Test split + combine
        for(int i = 1; i <= usersNumber; i++){
            System.out.println("**********NUOVO DOCUMENTO*************");
            SecureDistributedStorage.distributeShares("documenti/3_5/documento"+i+extensions.get(i), 3, 5, 
                    "keyring/Client"+ i +".kc", ("client"+i).toCharArray(), "clients/client"+i+"documento"+i+"3_5");
            System.out.println("Split Documento "+i+" OK !");
            System.out.println("Cancellazione documento originale...");
            new File("documenti/3_5/documento"+i+extensions.get(i)).delete();
            
            //Test combine per ogni valore di k
            for(int k = 1; k <= 5; k++){
                System.out.println("*******COMBINE CON K = "+k+"***************");
                if(k < 3)
                    System.out.println("NOTA: Il combine viene eseguito lo stesso. Controllare poi il file nella cartella per verificare che esso non è correttamente ricostruito.");
                ArrayList<BigInteger> servers = new ArrayList<>();
                for(int h = 1; h <= k; h++)
                    servers.add(BigInteger.valueOf(h));
                SecureDistributedStorage.restoreFromShares("clients/client"+i+"documento"+i+"3_5.info",servers, "keyring/Client"+ i +".kc", ("client"+i).toCharArray());
                new File("ricostruiti/documento"+i+extensions.get(i)).renameTo(new File("ricostruiti/documento"+i+"with_k_"+k+"3_5"+extensions.get(i)));
                System.out.println("OK !");
                
                //Test shares alterate e conseguente fallimento MAC: si simula da programma passando la chiave MAC di un altro utente
                System.out.println("Test shares alterate");
                List<BigInteger> fakes = SecureDistributedStorage.restoreFromShares("clients/client"+i+"documento"+i+"3_5.info",servers, "keyring/Client"+ (i+1) +".kc", ("client"+(i+1)).toCharArray());
            }   
        }
        System.out.println("***************FINE TEST******************");
    }
    
    //Si splitta infine documento6 per l'utente6. A mano si va a modificare il file
    //delle share e si verifica che esso non viene ricostruito.
    
}
