package ru.abrosimov.defi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.abrosimov.defi.exception.CommunicationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class   DefiLamaClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final static String GET_PROTOCOLS_URL = "https://api.llama.fi/protocols";
    private final static String GET_POOLS_URL = "https://yields.llama.fi/pools";

    public JsonNode getProtocols() throws JsonProcessingException {
        JsonNode jsonNode = getRequest(GET_PROTOCOLS_URL);

        if (jsonNode == null || !jsonNode.isArray()) {
            throw new CommunicationException("No data array found in response from API url: " + GET_PROTOCOLS_URL);
        }

        return jsonNode;
    }

    public JsonNode getPools() throws JsonProcessingException {
        JsonNode jsonNode = getRequest(GET_POOLS_URL);
        JsonNode dataNode = jsonNode.get("data");

        if (dataNode == null || !dataNode.isArray()) {
            throw new CommunicationException("No data array found in response from API url: " + GET_POOLS_URL);
        }

        return dataNode;
    }

    private JsonNode getRequest(String url) throws JsonProcessingException {
        String response = restTemplate.getForObject(url, String.class);

        if (response == null || response.isEmpty()) {
            throw new CommunicationException("Empty response from API url: " + url);
        }

        return objectMapper.readTree(response);
    }
}
