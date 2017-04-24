package pl.put.poznan.whereismymoney.crypto;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by Kheldar on 19-Apr-17.
 */
public class RSAKeyManager {

    public static final String PUBLIC_KEY_FILE = "public.key";
    public static final String PRIVATE_KEY_FILE = "private.key";
    public static final int KEYSIZE = 2048;
    public static final String RSA = "RSA";
    Key publicKey;
    Key privateKey;

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }


    public void generateRSAKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(KEYSIZE);
            KeyPair kp = keyPairGenerator.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void SaveRSAKeysToFiles() {
        try {
            KeyFactory fact = KeyFactory.getInstance(RSA);
            RSAPublicKeySpec pub = fact.getKeySpec(publicKey,
                    RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
                    RSAPrivateKeySpec.class);

            exportKeyToFile(PUBLIC_KEY_FILE, pub.getModulus(),
                    pub.getPublicExponent());
            exportKeyToFile(PRIVATE_KEY_FILE, priv.getModulus(),
                    priv.getPrivateExponent());

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    private void exportKeyToFile(String fileName,
                                 BigInteger mod, BigInteger exp) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)));
        try {
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    public PublicKey readPublicKeyFromFile(String keyFileName) throws IOException {
        InputStream in = Files.newInputStream(Paths.get(keyFileName));
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

     public PrivateKey readPrivateKeyFromFile(String keyFileName) throws IOException {
        InputStream in = Files.newInputStream(Paths.get(keyFileName));
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance(RSA);
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw new RuntimeException("Serialisation error", e);
        } finally {
            oin.close();
        }
    }
}
