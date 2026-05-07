package com.linguaai.service;

import com.linguaai.dto.TranslateRequest;
import com.linguaai.dto.TranslateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Calls the MyMemory free translation API.
 * Endpoint: GET https://api.mymemory.translated.net/get?q=Hello&langpair=en|ta
 * No API key required. Supports all 7 app languages.
 */
@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    @Value("${mymemory.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public TranslateResponse translate(TranslateRequest request) {
        log.info("Translation request -> text='{}', source='{}', target='{}'",
                request.getText(), request.getSourceLang(), request.getTargetLang());

        // MyMemory uses langpair format: "en|ta"
        String langPair = request.getSourceLang() + "|" + request.getTargetLang();

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("q", request.getText())
                .queryParam("langpair", langPair)
                .build()
                .toUriString();

        log.debug("Calling MyMemory URL: {}", url);

        // MyMemory returns: { "responseData": { "translatedText": "..." }, "responseStatus": 200 }
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            log.error("MyMemory returned null response");
            throw new RuntimeException("Empty response from translation API");
        }

        log.debug("MyMemory raw response: {}", response);

        Object status = response.get("responseStatus");
        log.info("MyMemory responseStatus: {}", status);

        Map<String, Object> responseData = (Map<String, Object>) response.get("responseData");
        if (responseData == null) {
            log.error("responseData is null in MyMemory response: {}", response);
            throw new RuntimeException("Invalid response structure from translation API");
        }

        String translatedText = (String) responseData.get("translatedText");
        if (translatedText == null || translatedText.isBlank()) {
            log.error("translatedText is null/blank. Full response: {}", response);
            throw new RuntimeException("Translation API returned empty text");
        }

        log.info("Translation success -> '{}'", translatedText);
        return new TranslateResponse(translatedText);
    }
}
