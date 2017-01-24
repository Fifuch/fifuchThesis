package pl.put.poznan.whereismymoney.security.authentication;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;

@Aspect
@Component
public class AuthenticationChecker {
    private UserRepository userRepository;
    private TokenCreator tokenCreator;
    
    @Autowired
    public AuthenticationChecker(UserRepository userRepository, TokenCreator tokenCreator) {
        this.userRepository = userRepository;
        this.tokenCreator = tokenCreator;
    }
    
    @Before("execution(public * pl.put.poznan.whereismymoney.service.secured.*.*(..))")
    public void checkToken(JoinPoint webServiceResponse) throws Throwable {
        String givenToken = getToken(webServiceResponse);
        String createdToken = createToken(webServiceResponse);
        if(!createdToken.equals(givenToken) || givenToken.equals("") || givenToken.equals(" ")) {
            throw new AuthenticationFailedException();
        }
    }
    
    private String getToken(JoinPoint webServiceResponse) {
        Object[] queryParameters = webServiceResponse.getArgs();
        String token = "";
        if(queryParameters.length > 1) {
            token = String.valueOf(queryParameters[1]);
        }
        return token;
    }
    
    private String createToken(JoinPoint webServiceResponse) {
        String token = "";
        Object[] queryParameters = webServiceResponse.getArgs();
        if(queryParameters.length > 0 && queryParameters[0] instanceof Long) {
            Long userId = (Long) queryParameters[0];
            User user = userRepository.findOne(userId);
            token = tokenCreator.createToken(user);
        }
        return token;
    }
}
