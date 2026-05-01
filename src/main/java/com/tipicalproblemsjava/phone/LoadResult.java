package com.tipicalproblemsjava.phone;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат выполнения нагрузочного endpoint")
public record LoadResult(
        @Schema(description = "Количество выполненных итераций", example = "100000000")
        long iterations,

        @Schema(description = "Контрольная сумма вычислений", example = "7123456789")
        long checksum,

        @Schema(description = "Время выполнения нагрузки в миллисекундах", example = "850")
        long elapsedMillis
) {
}
