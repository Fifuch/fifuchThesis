package pl.put.poznan.whereismymoney.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by Kheldar on 13-Dec-16.
 */

public class CryptoUtils {

    public static final String CHARSET_NAME = "UTF-8";
    public static final String AES_MODE = "AES/CBC/PKCS5Padding";
    public static final String AES = "AES";
    public static final String RSA = "RSA";

    public static SecretKey generateAESKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            return keygen.generateKey();
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] AesEncrypt(SecretKey key, byte[] data) {
        byte[] encrypted = null;
        try {
            Cipher aesCipher = Cipher.getInstance(AES_MODE);
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = aesCipher.doFinal(data);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return encrypted;

    }

    public static byte[] AesDecrypt(SecretKey key, byte[] data) {

        byte[] decrypted = null;
        try {
            Cipher aesCipher = Cipher.getInstance(AES_MODE);

            aesCipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = aesCipher.doFinal(data);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return decrypted;

    }

    public static byte[] RSAEncrypt(byte[] data, PublicKey key) {

        byte[] encrypted = null;
        try {
            final Cipher cipher = Cipher.getInstance(RSA);
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

            final Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, key);
            cipher.update(data);
            decrypted = cipher.doFinal();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return decrypted;


    }

}
