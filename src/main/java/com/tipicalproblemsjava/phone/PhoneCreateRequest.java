package com.tipicalproblemsjava.phone;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на создание или обновление телефона")
public record PhoneCreateRequest(
        @Schema(description = "Бренд телефона", example = "Apple", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Бренд должен быть заполнен")
        String brand,

        @Schema(description = "Модель телефона", example = "iPhone 15", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Модель должна быть заполнена")
        String model,

        @Schema(description = "Объем памяти телефона в GB", example = "128", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "storageGb не может быть null")
        @Min(value = 1, message = "storageGb должен быть больше нуля")
        Integer storageGb,

        @Schema(description = "Цена телефона в рублях", example = "79900", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "цена не может быть null")
        @Min(value = 1, message = "цена должна быть больше нуля")
        Integer price,

        @Schema(description = "Есть ли телефон в наличии", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "inStock не должен быть null")
        Boolean inStock
) {
}
