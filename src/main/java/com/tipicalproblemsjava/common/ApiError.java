package com.tipicalproblemsjava.common;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с описанием ошибки")
public record ApiError(
        @Schema(description = "Время возникновения ошибки")
        Instant timestamp,

        @Schema(description = "HTTP-статус", example = "400")
        int status,

        @Schema(description = "Краткое описание ошибки", example = "Validation failed")
        String error,

        @Schema(description = "Детали ошибки")
        List<String> details
) {

    public static ApiError of(int status, String error, List<String> details) {
        return new ApiError(Instant.now(), status, error, details);
    }
}
