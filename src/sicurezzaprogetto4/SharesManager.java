/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Daniele
 */
public class SharesManager {
    private final int bufferSize = 32;
    private final int modLength = bufferSize*8;
    public int k;
    public int n;
    private SecretSharing s;

    public SharesManager(int k, int n){
        this.k = k;
        this.n = n;
        this.s = new SecretSharing(k, n, this.modLength);
    }

    public int generateShares(String fileToSplit) throws IOException, Exception{
        //Inizializzazione stream
        BufferedInputStream is = null;
        ArrayList<BufferedOutputStream> outList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            outList.add(null);
        }
        int last = this.bufferSize;
        try{
            for(int i = 0; i < n; i++){
                outList.set(i,new BufferedOutputStream(new FileOutputStream((i+1)+"/"+fileToSplit)));
            }
            is= new BufferedInputStream(new FileInputStream(fileToSplit));
            //Generazione e scrittura shares
            HashMap<BigInteger,BigInteger> temp = null;
            byte[] buffer = new byte[this.bufferSize];
            int r;
            while((r = is.read(buffer, 0, buffer.length))!=-1){
                if(r < this.bufferSize){
                    //Per ultimo byte letto
                    last = r;
                    byte[] newbuffer = Arrays.copyOfRange(buffer, 0, r);
                    temp = s.split(newbuffer);
                }else
                    temp = s.split(buffer);
                 for(int i = 0; i < n; i++){
                    //Aggiunta padding e scrittura
                    byte[] padded = BigIntegerToByteArray(temp.get(BigInteger.valueOf(i+1)),this.bufferSize+1);
                    outList.get(i).write(padded);
                 }
            }
        }finally{
            if(is!=null)
                is.close();
            for(int i = 0; i < n; i++){
                BufferedOutputStream out = outList.get(i);
                if(out!=null)
                    out.close();
            }
        }
        return last;
    }

    public void reconstructFile(ArrayList<BigInteger> servers, String fileToCombine, int lastShareLength) throws FileNotFoundException, IOException{
        ArrayList<BufferedInputStream> inList = new ArrayList<>();
        for(int i = 0; i<servers.size(); i++){
            inList.add(null);
        }
        BufferedOutputStream out = null;
        try{
            out = new BufferedOutputStream(new FileOutputStream(fileToCombine+"pollo.pdf"));
            for(int i = 0; i<servers.size(); i++){
                inList.set(i,new BufferedInputStream(new FileInputStream((servers.get(i).intValue()+"/"+fileToCombine))));
            }
            long fileLength = new File(servers.get(0).intValue()+"/"+fileToCombine).length();
            HashMap<BigInteger,BigInteger> temp = new HashMap<>();
            byte[] buffer = new byte[this.bufferSize+1];
            long r = 0;
            while(this.bufferSize +1 != fileLength-r){
                for(int i = 0; i< servers.size(); i++){
                    inList.get(i).read(buffer, 0, buffer.length);
                    temp.put(servers.get(i), new BigInteger(1, buffer));
                }
                r+=(this.bufferSize + 1);
                BigInteger result = s.combine(temp);
                out.write(BigIntegerToByteArray(result, this.bufferSize));
            }
            //Lettura ultimo pezzo di ciascun file
            for(int i = 0; i< servers.size(); i++){
                inList.get(i).read(buffer, 0, buffer.length);
                temp.put(servers.get(i), new BigInteger(1, buffer));
            }
            BigInteger result = s.combine(temp);
            out.write(BigIntegerToByteArray(result, lastShareLength));
        }finally{
            if(out!=null)
                out.close();
            for(int i = 0; i < servers.size(); i++){
                BufferedInputStream bis = inList.get(i);
                if(bis!=null)
                    bis.close();
            }
        }
    }

    private byte[] BigIntegerToByteArray(BigInteger bi, int size){
        byte[] toEval = bi.toByteArray();
        byte[] padded = new byte[size];
        if(toEval.length > size)
           toEval = Arrays.copyOfRange(toEval, 1, padded.length+1);
        if(toEval.length < size){
           int i = padded.length-toEval.length;
           for(int j = 0; j < toEval.length; j++){
               padded[i] = toEval[j];
               i += 1;
           }
           toEval = padded;
        }
        return toEval;
    }
}
