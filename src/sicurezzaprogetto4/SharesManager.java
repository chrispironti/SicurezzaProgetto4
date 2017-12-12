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
import java.security.*;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import javax.crypto.*;
import org.json.*;

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
    
    public SharesManager(int k, BigInteger p){
        this.k = k;
        this.n = 0;
        this.s = new SecretSharing(k, 0, p, this.modLength);
    }

    public JSONObject generateShares(String fileToSplit, SecretKey key) throws IOException, Exception{
        if(n<=0){
            throw new NotEnoughServersException();
        }
        //Inizializzazione stream
        BufferedInputStream is = null;
        ArrayList<BufferedOutputStream> outList = new ArrayList<>();
        JSONObject j = new JSONObject();
        try{
            generateOutputStreams(fileToSplit);
            is = new BufferedInputStream(new FileInputStream(fileToSplit));
            //Generazione e scrittura shares
            j = this.splitFile(outList, is, key, fileToSplit);
        }finally{
            if(is!=null)
                is.close();
            for(int i = 0; i < n; i++){
                BufferedOutputStream out = outList.get(i);
                if(out!=null)
                    out.close();
            }
        }
        return j;
    }

    public List<BigInteger> reconstructFile(List<BigInteger> servers, List<byte[]> mac, String originalFile, int lastShareLength, SecretKey key) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeyException{
        //Inizializzazione Stream
        List<MacInputStream> inList = new ArrayList<>();
        BufferedOutputStream out = null;
        try{
            out = new BufferedOutputStream(new FileOutputStream("ricostruiti/"+originalFile));
            inList = this.generateInputStreams(originalFile, servers, key);   
            long originalLength=new File(originalFile).length();
            long fileLength=(originalLength/this.bufferSize)*(this.bufferSize+1);
            if(fileLength % this.bufferSize!= 0){
                fileLength+=this.bufferSize+1;
            }   
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
                out.write(SSUtils.BigIntegerToByteArray(result, this.bufferSize));
            }
            //Lettura ultimo pezzo di ciascun file
            for(int i = 0; i< servers.size(); i++){
                inList.get(i).read(buffer, 0, buffer.length);
                temp.put(servers.get(i), new BigInteger(1, buffer));
            }
            BigInteger result = s.combine(temp);
            out.write(SSUtils.BigIntegerToByteArray(result, lastShareLength));
        }finally{
            if(out!=null)
                out.close();
            for(int i = 0; i < servers.size(); i++){
                MacInputStream bis = inList.get(i);
                if(bis!=null)
                    bis.close();
            }
        }
        List<BigInteger> fakes=new ArrayList<>();
        for(int i=0; i<mac.size();i++){
            if(!SSUtils.byteArrayEquals(inList.get(i).getMac().doFinal(), mac.get(i))){
                fakes.add(BigInteger.valueOf(i));
            }       
        }
        return fakes;
    }

    private List<BufferedOutputStream> generateOutputStreams(String fileToSplit) throws NoSuchAlgorithmException, FileNotFoundException, IOException{
        ArrayList<BufferedOutputStream> outList = new ArrayList<>();
        for(int i = 0; i < n; i++)
            outList.add(null);
        String fileName;
        byte[] concat;
        MessageDigest md = MessageDigest.getInstance("SHA256");
        //File da salvare sui server come H(fileoriginario|IDServer|fileoriginario)
        for(int i = 0; i < n; i++){
            concat = SSUtils.arrayConcat(fileToSplit.getBytes(), new String(""+(i+1)).getBytes());
            fileName = new String(md.digest(SSUtils.arrayConcat(concat, fileToSplit.getBytes())));
            outList.set(i,new BufferedOutputStream(new FileOutputStream("servers/"+(i+1)+"/"+fileName)));
        }
        return outList;
    }
    
    private List<MacInputStream> generateInputStreams(String originalFile, List<BigInteger> servers, SecretKey key) throws NoSuchAlgorithmException, FileNotFoundException, IOException, InvalidKeyException{
        Mac mac;
        ArrayList<MacInputStream> inList = new ArrayList<>();
        for(int i = 0; i < servers.size(); i++)
            inList.add(null);
        String fileName = "";
        byte[] concat;
        MessageDigest md = MessageDigest.getInstance("SHA256");
        for(int i = 0; i < servers.size(); i++){
            mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            concat = SSUtils.arrayConcat(originalFile.getBytes(), new String(""+servers.get(i).intValue()).getBytes());
            fileName = new String(md.digest(SSUtils.arrayConcat(concat, originalFile.getBytes())));
            BufferedInputStream is = new BufferedInputStream(new FileInputStream("servers/"+servers.get(i).intValue()+"/"+fileName));
            MacInputStream mis= new MacInputStream(is, mac);
            inList.set(i,mis);
        }
        return inList;
    }
    
    private JSONObject splitFile(ArrayList<BufferedOutputStream> outList, BufferedInputStream is, SecretKey key, String fileName) throws IOException, Exception{
        int last = this.bufferSize;
        JSONObject j = new JSONObject();
        ArrayList<Mac> macList = new ArrayList<>();
        Mac mac;
        //Inizializzazione MAC per ogni n
        for(int i = 0; i < n; i++){
            mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            macList.add(mac);
        }
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
                //Aggiunta padding, scrittura e aggiornamento Mac
                byte[] padded = SSUtils.BigIntegerToByteArray(temp.get(BigInteger.valueOf(i+1)),this.bufferSize+1);
                outList.get(i).write(padded);
                macList.get(i).update(padded);
            }
        }
        //Scrittura Mac nel JSONArray e restituzione JSONObject
        JSONArray a = new JSONArray();
        for(int i = 0; i < n; i++)
            a.put(i, Base64.getEncoder().encode(macList.get(i).doFinal()));
        j.put(("FileName"), fileName);
        j.put("RestoreNum", k);
        j.put("MacList", a);
        j.put("LastBufferDim", last);
        j.put("Prime", Base64.getEncoder().encode(s.getPrime().toByteArray()));
        return j;
    }
}
