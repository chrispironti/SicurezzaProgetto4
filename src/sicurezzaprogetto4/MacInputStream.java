/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.Mac;

/**
 *
 * @author Daniele
 */
public class MacInputStream extends FilterInputStream {
    
    private Mac mac;    
    
    public MacInputStream(InputStream is, Mac mac){
        super(is);
        this.mac = mac;
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        
        int n = in.read(b, off, len); 
        if(n >= 0){
            mac.update(b, off, n);
        }
        return n;
    }

    @Override
    public int read() throws IOException {
        
        int b = in.read();
        if(b >= 0)
            mac.update((byte)b);
        return b;
    }
    
    public Mac getMac(){
        return mac;
    }
}
