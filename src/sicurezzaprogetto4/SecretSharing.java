package sicurezzaprogetto4;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.math.BigInteger;
import java.util.*;

/**
 *
 * 
 */
public class SecretSharing {
    
    private final int CERTAINTY = 50;
    public int k;
    public int n;
    private int modLength;
    private BigInteger p;
    
    public SecretSharing(int k, int n){
        this.k = k;
        this.n = n;
    }
    
    public HashMap<BigInteger, BigInteger> split(byte[] secretInformation){
        
        BigInteger secret = new BigInteger(secretInformation);
        this.modLength = secret.bitLength();
        this.p = genPrime();
        ArrayList<BigInteger> poly = new ArrayList<>();
        poly.add(secret);
        //Calcolo coefficienti ai del polinomio
        for(int i = 0; i < this.k - 1; i++){
            poly.add(randomZp());
        }
        //Calcolo shares
        HashMap<BigInteger, BigInteger> shares = new HashMap<>();
        for(int i = 1; i <= this.n ; i++){
            shares.put(BigInteger.valueOf(i),evalPoly(BigInteger.valueOf(i), poly));
        }
        return shares;
    }
    
    public byte[] combine(HashMap<BigInteger, BigInteger> shares){
        BigInteger secret = BigInteger.ZERO;
        BigInteger den;
        if(shares.size() < this.k)
            System.out.println("Attenzione ! Numero di shares non sufficienti per ricostruire l'informazione originale");
        for(Map.Entry<BigInteger, BigInteger> e1: shares.entrySet()){
            BigInteger id1 = e1.getKey();
            BigInteger num = e1.getValue();
            for(Map.Entry<BigInteger, BigInteger> e2: shares.entrySet()){
                BigInteger id2 = e2.getKey();
                if(id2.compareTo(id1) != 0){
                    num = (num.multiply(id2.negate())).mod(this.p);
                    den = (id1.subtract(id2)).mod(this.p);
                    num = (num.multiply((den.modInverse(this.p)))).mod(this.p);
                }
            }
            secret = (secret.add(num)).mod(this.p);
        }
        return secret.toByteArray();
    }
    
    private BigInteger genPrime(){
        
        BigInteger p = null;
        boolean isPrime = false;
        do
        {
            p=BigInteger.probablePrime((this.modLength + 1), new Random());
            if(p.isProbablePrime(this.CERTAINTY))
                isPrime = true;
        }
        while(isPrime == false);
        return p;
    }
    
    private BigInteger randomZp() {
        
        BigInteger r;
        do 
        {
            r = new BigInteger(this.modLength, new Random());
        }
        while (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(this.p) >= 0);
        return r;
    }
    
    private BigInteger evalPoly(BigInteger x, ArrayList<BigInteger> poly){
        
        BigInteger result = BigInteger.ZERO;
        BigInteger pow, mul;
        for(int i = 0 ; i < poly.size(); i++){ //poly.size sarebbe k?
            pow = x.modPow(BigInteger.valueOf(i), this.p);
            mul = (poly.get(i).multiply(pow)).mod(this.p);
            result = (result.add(mul)).mod(this.p);
        }
        return result;
    }
}