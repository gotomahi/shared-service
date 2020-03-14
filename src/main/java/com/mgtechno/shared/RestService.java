package com.mgtechno.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RestService {
    private static final Logger LOG = LoggerFactory.getLogger(RestService.class);

    public Map invokePostService(String url, Map<String, String> paramValues, Map<String, String> headerValues)throws IOException{
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(getServiceParams(paramValues), getServiceHeaders(headerValues));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return getBody(response);
    }

    public Map invokeGetService(String url, Map<String, String> paramValues, Map<String, String> headerValues)throws IOException{
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(getServiceParams(paramValues), getServiceHeaders(headerValues));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return getBody(response);
    }

    private Map getBody(ResponseEntity<String> response)throws IOException {
        Map result = null;
        if(response.getStatusCodeValue() == 200 && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            LOG.info("Response body is "+ response.getBody());
            result = mapper.readValue(response.getBody(), Map.class);
        }
        return result;
    }

    private MultiValueMap<String, String> getServiceParams(Map<String, String> paramValues){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for(String param: paramValues.keySet()){
            List<String> paramValueList = new ArrayList<>();
            paramValueList.add(paramValues.get(param));
            params.put(param, paramValueList);
        }
        return params;
    }

    private HttpHeaders getServiceHeaders(Map<String, String> headerValues){
        HttpHeaders headers = new HttpHeaders();
        for(String header: headerValues.keySet()){
            List<String> headerValueList = new ArrayList<>();
            headerValueList.add(headerValues.get(header));
            headers.put(header, headerValueList);
        }
        return headers;
    }
}
