package pl.put.poznan.whereismymoney.http;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import pl.put.poznan.whereismymoney.crypto.CryptoUtils;
import pl.put.poznan.whereismymoney.dao.BudgetDao;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCommunicator {
    private HttpClient httpClient;
    private StringBuilder responseContent;
    private Gson gson;

    @Inject
    public ServerCommunicator(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    public String sendMessageAndWaitForResponse(String uri, Map<String, String> parameters) throws IOException {
        HttpPost serverQuery = prepareServerQuery(uri, parameters);
        HttpResponse response = httpClient.execute(serverQuery);
        return parseResponse(response);
    }

    private HttpPost prepareServerQuery(String uri, Map<String, String> parameters) throws UnsupportedEncodingException {
        List<NameValuePair> entityMatchingParameters = new ArrayList<>(parameters.size());
        for(String name : parameters.keySet()) {
            entityMatchingParameters.add(new BasicNameValuePair(name, parameters.get(name)));
        }
        HttpPost serverQuery = new HttpPost(uri);
        serverQuery.setEntity(new UrlEncodedFormEntity(entityMatchingParameters, "UTF-8"));
        return serverQuery;
    }

    private String parseResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        responseContent = new StringBuilder("");
        if (entity != null) {
            try(BufferedReader responseBody = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                responseBody.lines().forEach(line -> responseContent.append(line));
            }
        }
        return responseContent.toString();
    }

    public Map<String, String> provideBasicParameters(SessionManager sessionManager, SecretKey aesKey, IvParameterSpec iv) {
        Map<String, String> parameters = new HashMap<>(2);
        parameters.put("sessionKey", CryptoUtils.encryptParameter(sessionManager.getSessionKey(),aesKey,iv));
        parameters.put("username", CryptoUtils.encryptParameter(sessionManager.getUserData().getUsername(),aesKey,iv));
        return parameters;
    }

    public Map<String, String> provideEncryptionParameters(SecretKey aesKey, IvParameterSpec iv) {
        Map<String, String> parameters = new HashMap<>(2);
        parameters.put("cipherKey",CryptoUtils.encryptKey(aesKey));
        parameters.put("iv",CryptoUtils.encryptIv(iv));

        return parameters;


    }

    }
