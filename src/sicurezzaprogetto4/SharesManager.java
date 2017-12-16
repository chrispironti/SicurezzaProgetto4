/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import javax.crypto.*;
import org.json.*;

/**
 *
 * @author Daniele
 */
public class SharesManager {
    private final int bufferSize = 32;
    private final int modLength = bufferSize*8;
    private final int randomSize = 8;
    public int k;
    public int n;
    private SecretSharing s;

    public SharesManager(int k, int n){
        this.k = k;
        this.n = n;
        this.s = new SecretSharing(k, n, this.modLength);
    }
    
    //Da non usare per operazioni di split
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
        ArrayList<BufferedOutputStream> outList = null;
        JSONObject j = new JSONObject();
        try{
            String[] originalFileName = fileToSplit.split("/");
            byte[] random = new byte[this.randomSize];
            new SecureRandom().nextBytes(random);
            outList = generateOutputStreams(originalFileName[originalFileName.length -1], random);
            is = new BufferedInputStream(new FileInputStream(fileToSplit));
            //Generazione e scrittura shares
            j = this.splitFile(outList, is, key);
            j.put("Prime", Base64.getEncoder().encodeToString(s.getPrime().toByteArray()));
            j.put("FileName", originalFileName[originalFileName.length -1]);
            j.put("RestoreNum", this.k);
            j.put("Random", Base64.getEncoder().encodeToString(random));
        }finally{
            if(is!=null)
                is.close();
            if(outList != null){
                for(int i = 0; i < n; i++){
                    BufferedOutputStream out = outList.get(i);
                    if(out!=null)
                        out.close();
                }
            }
        }
        return j;
    }
    
    private JSONObject splitFile(ArrayList<BufferedOutputStream> outList, BufferedInputStream is, SecretKey key) throws IOException, Exception{
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
            a.put(i, Base64.getEncoder().encodeToString(macList.get(i).doFinal()));
        j.put("MacList", a);
        j.put("LastBufferDim", last);
        return j;
    }
        
    public ArrayList<BigInteger> reconstructFile(ArrayList<BigInteger> servers, ArrayList<byte[]> mac, String originalFileName, int lastShareLength, SecretKey key, byte[] random) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeyException{
        //Inizializzazione Stream
        ArrayList<MacInputStream> inList = null;
        BufferedOutputStream out = null;
        try{
            out = new BufferedOutputStream(new FileOutputStream("ricostruiti/"+originalFileName));
            inList = this.generateInputStreams(originalFileName, servers, key, random);
            //Combine shares
            this.combineShares(inList, out, servers, lastShareLength);
        }finally{
            if(out!=null)
                out.close();
            if(inList != null){
                for(int i = 0; i < servers.size(); i++){
                    MacInputStream mis = inList.get(i);
                    if(mis!=null)
                        mis.close();
                }
            }
        }
        //Verifica eventuali shares corrotte
        ArrayList<BigInteger> fakes=new ArrayList<>();
        for(int i=0; i<mac.size();i++){
            if(!SSUtils.byteArrayEquals(inList.get(i).getMac().doFinal(), mac.get(i))){
                fakes.add(BigInteger.valueOf(i));
            }       
        }
        return fakes;
    }
    
    private void combineShares(ArrayList<MacInputStream> inList, BufferedOutputStream out, ArrayList<BigInteger> servers, int lastShareLength) throws IOException{
        
        HashMap<BigInteger,BigInteger> temp = new HashMap<>();
        byte[] buffer = new byte[this.bufferSize+1];
        //Lettura e combine delle share
        while(this.bufferSize +1 < inList.get(0).available()){
            for(int i = 0; i< servers.size(); i++){
                inList.get(i).read(buffer, 0, buffer.length);
                temp.put(servers.get(i), new BigInteger(1, buffer));
            }
            BigInteger result = s.combine(temp);
            out.write(SSUtils.BigIntegerToByteArray(result, this.bufferSize));
        }
        //Lettura e combine ultimi bufferSize+1 bytes di ciascun file
        for(int i = 0; i< servers.size(); i++){
            inList.get(i).read(buffer, 0, buffer.length);
            temp.put(servers.get(i), new BigInteger(1, buffer));
        }
        BigInteger result = s.combine(temp);
        //Recupero file originario
        out.write(SSUtils.BigIntegerToByteArray(result, lastShareLength));
    }
   
    private ArrayList<BufferedOutputStream> generateOutputStreams(String fileToSplit, byte[] random) throws NoSuchAlgorithmException, FileNotFoundException, IOException{
        ArrayList<BufferedOutputStream> outList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            outList.add(new BufferedOutputStream(new FileOutputStream("servers/"+(i+1)+"/"+SSUtils.generateFileName(fileToSplit, i+1, random))));
        }
        return outList;
    }
    
    private ArrayList<MacInputStream> generateInputStreams(String originalFile, ArrayList<BigInteger> servers, SecretKey key, byte[] random) throws NoSuchAlgorithmException, FileNotFoundException, IOException, InvalidKeyException{
        Mac mac;
        ArrayList<MacInputStream> inList = new ArrayList<>();
        //Generazione MacInputStream
        for(int i = 0; i < servers.size(); i++){
            mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            BufferedInputStream is = new BufferedInputStream(new FileInputStream("servers/"+servers.get(i).intValue()+"/"+SSUtils.generateFileName(originalFile, servers.get(i).intValue(), random)));
            MacInputStream mis= new MacInputStream(is, mac);
            inList.add(mis);
        }
        return inList;
    }
}