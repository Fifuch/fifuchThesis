package pl.put.poznan.thesis.crypto;

import com.google.gson.Gson;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Kheldar on 13-Dec-16.
 */

public class Encryption {

    public static final String AES_MODE = "AES/CBC/PKCS5Padding";
    public static final String AES = "AES";
    public static final String RSA = "RSA/ECB/PKCS1Padding";
    public static final String CHARSET = "UTF-8";

    public static SecretKey generateAESKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            return keygen.generateKey();
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] AesEncrypt(SecretKey key,IvParameterSpec iv, byte[] data) {
        byte[] encrypted = null;
        try {
            Cipher aesCipher = Cipher.getInstance(AES_MODE);
            aesCipher.init(Cipher.ENCRYPT_MODE, key,iv);
            encrypted = aesCipher.doFinal(data);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return encrypted;

    }

    public static byte[] AesDecrypt(SecretKey key, IvParameterSpec iv, byte[] data) {

        byte[] decrypted = null;
        try {
            Cipher aesCipher = Cipher.getInstance(AES_MODE);

            aesCipher.init(Cipher.DECRYPT_MODE, key,iv);
            decrypted = aesCipher.doFinal(data);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return decrypted;

    }

    public static byte[] RSAEncrypt(byte[] data, PublicKey key) {

        byte[] encrypted = null;
        try {
            final Cipher cipher = Cipher.getInstance(Encryption.RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher.update(data);
            encrypted = cipher.doFinal();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return encrypted;

    }

    public static byte[] RSADecrypt(byte[] data, PrivateKey key) {

        byte[] decrypted = null;
        try {

            final Cipher cipher = Cipher.getInstance(Encryption.RSA);
            cipher.init(Cipher.DECRYPT_MODE, key);
            cipher.update(data);
            decrypted = cipher.doFinal();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return decrypted;


    }

    public static SecretKey decryptAesKey(String key,PrivateKey privateKey){
        Gson gson = new Gson();
        byte[] rsaEncrypteddKey = gson.fromJson(key, byte[].class);
        byte[] decodedKey = RSADecrypt(rsaEncrypteddKey, privateKey);
        return new SecretKeySpec(decodedKey,"AES");
    }

    public static IvParameterSpec decryptIv(String iv, PrivateKey privateKey){
        Gson gson = new Gson();
        byte[] encryptedIv = gson.fromJson(iv,byte[].class);
        return new IvParameterSpec(RSADecrypt(encryptedIv,privateKey));
    }

    public static String decryptStringParameter(String string, SecretKey aesKey, IvParameterSpec ivParameter){
        Gson gson = new Gson();
        try {
            return new String(AesDecrypt(aesKey, ivParameter, gson.fromJson(string, byte[].class)), CHARSET);
        }catch (Exception e){
            return "";
        }
    }

    public static byte[] decryptByteArrayParameter(String byteArray, SecretKey aesKey, IvParameterSpec ivParameter){
        Gson gson = new Gson();
        return AesDecrypt(aesKey, ivParameter, gson.fromJson(byteArray, byte[].class));
    }

    public static String encryptParameter(String string, SecretKey key, IvParameterSpec iv) {
        try {
            Gson gson = new Gson();
            return gson.toJson(AesEncrypt(key, iv, string.getBytes(CHARSET)));
        } catch (Exception e) {
            return null;
        }

    }

    public static String encryptParameter(byte[] byteArray, SecretKey key, IvParameterSpec iv) {
        Gson gson = new Gson();
        return  gson.toJson(AesEncrypt(key, iv, byteArray));

    }

}
