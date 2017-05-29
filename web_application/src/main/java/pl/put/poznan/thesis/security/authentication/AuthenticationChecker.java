package pl.put.poznan.thesis.security.authentication;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.poznan.thesis.crypto.Encryption;
import pl.put.poznan.thesis.crypto.RSAKeyManager;
import pl.put.poznan.thesis.dao.UserRepository;
import pl.put.poznan.thesis.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;

@Aspect
@Component
public class AuthenticationChecker {
    private UserRepository userRepository;
    private SessionKeyGenerator sessionKeyGenerator;
    private Gson gson;
    private RSAKeyManager rsaKeyManager;

    @Autowired
    public AuthenticationChecker(UserRepository userRepository, SessionKeyGenerator sessionKeyGenerator, Gson gson) {
        this.userRepository = userRepository;
        this.sessionKeyGenerator = sessionKeyGenerator;
        this.gson = gson;
        this.rsaKeyManager = new RSAKeyManager();
    }

    @Before("execution(public * pl.put.poznan.thesis.service.secured.*.*(..))")
    public void checkToken(JoinPoint webServiceResponse) throws Throwable {
        byte[] obtainedSessionKey = obtainSessionKey(webServiceResponse);
        byte[] createdSessionKey = generateSessionKey(webServiceResponse);
        if (!Arrays.equals(obtainedSessionKey, createdSessionKey) && obtainedSessionKey.length > 0) {
            throw new AuthenticationFailedException();
        }
    }

    private byte[] obtainSessionKey(JoinPoint webServiceResponse) {
        Object[] queryParameters = webServiceResponse.getArgs();
        byte[] sessionKey = new byte[0];
        if (queryParameters.length > 0) {
            SecretKey key = Encryption.decryptAesKey(String.valueOf(queryParameters[queryParameters.length-2]),rsaKeyManager.getPrivateKey());
            IvParameterSpec iv= Encryption.decryptIv(String.valueOf(queryParameters[queryParameters.length-1]),rsaKeyManager.getPrivateKey());
            sessionKey = Encryption.decryptByteArrayParameter(String.valueOf(queryParameters[0]),key,iv);
        }
        return sessionKey;
    }

    private byte[] generateSessionKey(JoinPoint webServiceResponse) {
        byte[] sessionKey = new byte[0];
        Object[] queryParameters = webServiceResponse.getArgs();
        if (queryParameters.length > 1 && queryParameters[1] instanceof String) {
            SecretKey key = Encryption.decryptAesKey(String.valueOf(queryParameters[queryParameters.length-2]),rsaKeyManager.getPrivateKey());
            IvParameterSpec iv= Encryption.decryptIv(String.valueOf(queryParameters[queryParameters.length-1]),rsaKeyManager.getPrivateKey());
            String username = Encryption.decryptStringParameter(String.valueOf(queryParameters[1]),key,iv);
            User user = userRepository.findByUsername(username);
            sessionKey = sessionKeyGenerator.generate(user);
        }
        return sessionKey;
    }
}
