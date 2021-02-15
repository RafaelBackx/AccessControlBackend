package hhh.acs.model;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class BiostarAPIRequests {
    private final String biostarApiUrl;
    private String sessionId;

    public  BiostarAPIRequests(String biostarApiUrl){
        this.biostarApiUrl = biostarApiUrl;
    }

    public String logIn(String username, String password) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String url = this.biostarApiUrl +"/api/login";
        String body = "{\n" +
                "  \"User\": {\n" +
                "    \"login_id\": \""+ username +"\",\n" +
                "    \"password\": \""+password + "\"\n" +
                "  }\n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body,headers);
        RestTemplate restTemplate = getRestTemplate();
        HttpEntity<String> result = restTemplate.exchange(url, HttpMethod.POST,request,String.class);
        this.sessionId = result.getHeaders().get("bs-session-id").get(0);
        return result.getBody();
    }

    public void lockUnlockReleaseDoor(int[] doorids, Mode mode) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String url = this.biostarApiUrl + "/api/doors/" + mode.getCommand();
        List<String> rows = new ArrayList<>();
        for (int doorid : doorids){
            rows.add("{\"id\": "+ doorid +"}");
        }
        String body = "{\n" +
                "  \"DoorCollection\": {\n" +
                "    \"total\": "+ rows.size() +",\n" +
                "    \"rows\": " + rows + ""+
                "  }\n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("bs-session-id",this.sessionId);
        HttpEntity<String> request = new HttpEntity<>(body,headers);
        RestTemplate restTemplate = getRestTemplate();
        HttpEntity<String> result = restTemplate.exchange(url,HttpMethod.POST,request,String.class);
        System.out.println(result.getBody());
    }

    public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        };
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}
