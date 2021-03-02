package hhh.acs.model;

import hhh.acs.configuration.BackendProperties;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
    private BackendProperties backend;

    public  BiostarAPIRequests(String biostarApiUrl, BackendProperties backend){
        this.biostarApiUrl = biostarApiUrl;
        this.backend = backend;
    }

    /**
     *
     * @param username username van de gebruiker waarmee ingelogd moet worden
     * @param password wachtwoord van de user waarmee ingelogd moet worden
     * @return deze functie returnt de "bs-session-id" header die gebruikt wordt als API key in alle post requests
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */


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
        return this.sessionId;
    }

    /**
     *
     * @param doorids array van deur id's die geopend, gelocked of gereleased moeten worden
     * @param mode wat moet er met de deurs gebeuren? openen, locken of releasen
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */

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
        try{
            HttpEntity<String> result = restTemplate.exchange(url,HttpMethod.POST,request,String.class);
        }catch (HttpClientErrorException | HttpServerErrorException error){
            if (error.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("session invalidated, logging back in");
                logIn(backend.getUsername(), backend.getPassword());
                lockUnlockReleaseDoor(doorids, mode);
            } else {
                throw error;
            }
        }
    }

    public String forwardRequest(JSONObject json) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String method = json.getString("method");
        String body = json.getString("body");
        String url = json.getString("url");
        String apiKey = json.getString("apiKey");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("bs-session-id",apiKey);
        HttpEntity<String> request = new HttpEntity<>(body,headers);
        RestTemplate restTemplate = getRestTemplate();
        HttpMethod httpMethod = HttpMethod.GET;
        switch (method){
            case "get":
                httpMethod = HttpMethod.GET;
                break;
            case "post":
                httpMethod = HttpMethod.POST;
                break;
            case "put":
                httpMethod = HttpMethod.PUT;
                break;
        }
        HttpEntity<String> result = restTemplate.exchange(url,httpMethod,request,String.class);
        return result.getBody();
    }

    /**
     * Helper functie die ssl verificatie trust om calls te maken naar de biostar api die op https draait
     * @return deze functie returned een resttemplate waarmee alle api requests gemaakt worden
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */

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
