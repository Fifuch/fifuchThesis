package pl.put.poznan.whereismymoney.security.authentication;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;

import java.util.Arrays;

@Aspect
@Component
public class AuthenticationChecker {
    private UserRepository userRepository;
    private SessionKeyGenerator sessionKeyGenerator;
    private Gson gson;

    @Autowired
    public AuthenticationChecker(UserRepository userRepository, SessionKeyGenerator sessionKeyGenerator, Gson gson) {
        this.userRepository = userRepository;
        this.sessionKeyGenerator = sessionKeyGenerator;
        this.gson = gson;
    }

    @Before("execution(public * pl.put.poznan.whereismymoney.service.secured.*.*(..))")
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
            sessionKey = gson.fromJson(String.valueOf(queryParameters[0]), byte[].class);
        }
        return sessionKey;
    }

    private byte[] generateSessionKey(JoinPoint webServiceResponse) {
        byte[] sessionKey = new byte[0];
        Object[] queryParameters = webServiceResponse.getArgs();
        if (queryParameters.length > 1 && queryParameters[1] instanceof String) {
            String username = String.valueOf(queryParameters[1]);
            User user = userRepository.findByUsername(username);
            sessionKey = sessionKeyGenerator.generate(user);
        }
        return sessionKey;
    }
}
