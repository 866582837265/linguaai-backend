package com.linguaai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for sending translated text back to the frontend.
 */
@Data
@AllArgsConstructor
public class TranslateResponse {
    private String translatedText;
}
