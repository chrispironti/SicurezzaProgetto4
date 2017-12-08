/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author gennaroavitabile
 */
public class SecureDistributedStorage implements Serializable{

    private String nomeFile;
    //Inormazione per recuperare il file nei server?
    private String mac; //Non sono sicuro debba essere una stringa
    private List<String> servers;
    private int k;

    public SecureDistributedStorage(String nomeFile, List<String> servers, int k) {
        this.nomeFile = nomeFile;
        this.servers = servers;
        this.k = k;
    }
    
    
    public SecureDistributedStorage(String nomeFileRipristino){
        
    }
    
    public void distributeShares() throws FileNotFoundException, IOException{
        //SecretSharing s = new SecretSharing(k, servers.size());
        int currentSecret=0;
        BufferedInputStream is= new BufferedInputStream(new FileInputStream(nomeFile));
        
        
        
        while(currentSecret!=-1){
            currentSecret=is.read();
            //write negli stream se il valore non è -1
        }
        
        
        
    }
    
    
    
}
