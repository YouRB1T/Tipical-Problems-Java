package com.tipicalproblemsjava.phone;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными телефона")
public record PhoneResponse(
        @Schema(description = "Идентификатор телефона", example = "1")
        Long id,

        @Schema(description = "Бренд телефона", example = "Apple")
        String brand,

        @Schema(description = "Модель телефона", example = "iPhone 15")
        String model,

        @Schema(description = "Объем памяти телефона в GB", example = "128")
        Integer storageGb,

        @Schema(description = "Цена телефона в рублях", example = "79900")
        Integer price,

        @Schema(description = "Есть ли телефон в наличии", example = "true")
        boolean inStock
) {

    public static PhoneResponse from(Phone phone) {
        return new PhoneResponse(
                phone.getId(),
                phone.getBrand(),
                phone.getModel(),
                phone.getStorageGb(),
                phone.getPrice(),
                phone.isInStock()
        );
    }
}
