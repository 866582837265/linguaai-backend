package com.linguaai.controller;

import com.linguaai.dto.TranslateRequest;
import com.linguaai.dto.TranslateResponse;
import com.linguaai.service.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller exposing POST /api/translate.
 */
@RestController
@RequestMapping("/api")
public class TranslationController {

    private static final Logger log = LoggerFactory.getLogger(TranslationController.class);

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/translate")
    public ResponseEntity<?> translate(@RequestBody TranslateRequest request) {
        log.info("POST /api/translate received: text='{}', source='{}', target='{}'",
                request.getText(), request.getSourceLang(), request.getTargetLang());

        // Validate request fields
        if (request.getText() == null || request.getText().isBlank()) {
            log.warn("Rejected: empty text field");
            return ResponseEntity.badRequest().body(Map.of("error", "Text field is required"));
        }
        if (request.getSourceLang() == null || request.getTargetLang() == null) {
            log.warn("Rejected: missing language fields");
            return ResponseEntity.badRequest().body(Map.of("error", "sourceLang and targetLang are required"));
        }

        try {
            TranslateResponse response = translationService.translate(request);
            log.info("POST /api/translate success -> '{}'", response.getTranslatedText());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Translation error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
