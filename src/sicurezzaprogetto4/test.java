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
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        byte[] test = "abcdefgh".getBytes();        
        BigInteger b = new BigInteger(1, test);
        byte[] converted = b.toByteArray();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("testobj"));
        FileOutputStream fout = new FileOutputStream("testbyte");
        oos.writeObject(b);
        fout.write(test);
        oos.close();
        fout.close();
    }
    
}
