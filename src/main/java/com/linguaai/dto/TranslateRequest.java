package com.linguaai.dto;

import lombok.Data;

/**
 * DTO for incoming translation request from the frontend.
 */
@Data
public class TranslateRequest {
    private String text;
    private String sourceLang;
    private String targetLang;
}
