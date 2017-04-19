package pl.put.poznan.whereismymoney.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerCommunicator {
    private HttpClient httpClient;
    private StringBuilder responseContent;

    @Inject
    public ServerCommunicator(HttpClient httpClient) {
        this.httpClient = httpClient;
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
}
