package com.mgtechno.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestService {
    private static final Logger LOG = LoggerFactory.getLogger(RestService.class);

    /**
     * This method takes query parameters and headers
     *
     * @param url
     * @param paramValues
     * @param headerValues
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> ResponseEntity<T> post(String url, Map<String, Object> paramValues, Map<String, String> headerValues, Class<T> responseType) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(getServiceParams(paramValues), getServiceHeaders(headerValues));
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response;
    }

    public <T> ResponseEntity<T> get(String url, Map<String, Object> queryParams, Map<String, String> headerValues, Class<T> responseType) throws IOException {
        StringBuilder urlStr = new StringBuilder(url);
        if (!CollectionUtils.isEmpty(queryParams)) {
            queryParams.forEach((k, v) -> {
                urlStr.append(urlStr.indexOf("?") == -1 ? "?" : "&").append(k).append("=").append(v);
            });
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(null, getServiceHeaders(headerValues));
        ResponseEntity<T> response = restTemplate.exchange(urlStr.toString(), HttpMethod.GET, entity, responseType);
        return response;
    }

    public <T> ResponseEntity<T> put(String url, Map<String, Object> paramValues, Map<String, String> headerValues, Class<T> responseType) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(getServiceParams(paramValues), getServiceHeaders(headerValues));
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        return response;
    }

    private Map<String, Object> getServiceParams(Map<String, Object> paramValues) {
        Map<String, Object> params = new HashMap<>();
        if (!CollectionUtils.isEmpty(paramValues)) {
            for (String param : paramValues.keySet()) {
                List paramValueList = new ArrayList<>();
                paramValueList.add(paramValues.get(param));
                params.put(param, paramValueList);
            }
        }
        return params;
    }

    private HttpHeaders getServiceHeaders(Map<String, String> headerValues) {
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(headerValues)) {
            for (String header : headerValues.keySet()) {
                List<String> headerValueList = new ArrayList<>();
                headerValueList.add(headerValues.get(header));
                headers.put(header, headerValueList);
            }
        }
        return headers;
    }
}
