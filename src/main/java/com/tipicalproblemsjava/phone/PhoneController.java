package com.tipicalproblemsjava.phone;

import java.net.URI;
import java.util.List;

import com.tipicalproblemsjava.common.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/phones")
@Tag(name = "Телефоны", description = "CRUD API для работы с телефонами")
public class PhoneController {

    private final PhoneService phoneService;

    public PhoneController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @GetMapping
    @Operation(
            summary = "Получить список телефонов",
            description = "Возвращает все телефоны, которые сейчас сохранены в runtime H2 базе."
    )
    @ApiResponse(responseCode = "200", description = "Список телефонов успешно получен")
    public List<PhoneResponse> findAll() {
        return phoneService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить телефон по id",
            description = "Возвращает один телефон по его идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Телефон найден"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Телефон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public PhoneResponse findById(
            @Parameter(description = "Идентификатор телефона", example = "1")
            @PathVariable Long id
    ) {
        return phoneService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Создать телефон",
            description = "Создает новый телефон и сохраняет его в H2 базе."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Телефон создан"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<PhoneResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового телефона",
                    required = true
            )
            @Valid @RequestBody PhoneCreateRequest request
    ) {
        PhoneResponse created = phoneService.create(request);
        return ResponseEntity
                .created(URI.create("/api/phones/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить телефон",
            description = "Полностью обновляет данные телефона по id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Телефон обновлен"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Телефон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public PhoneResponse update(
            @Parameter(description = "Идентификатор телефона", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные телефона",
                    required = true
            )
            @Valid @RequestBody PhoneCreateRequest request
    ) {
        return phoneService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить телефон",
            description = "Удаляет телефон из H2 базы по id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Телефон удален"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Телефон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор телефона", example = "1")
            @PathVariable Long id
    ) {
        phoneService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/load")
    @Operation(
            summary = "Создать повышенную CPU-нагрузку",
            description = """
                    Выполняет долгий цикл с вычислением checksum.
                    Endpoint нужен для демонстрации нагрузки на сервис и снятия thread dump.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Нагрузка выполнена"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректное количество итераций",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public LoadResult createLoad(
            @Parameter(
                    description = "Количество итераций нагрузочного цикла",
                    example = "100000000"
            )
            @RequestParam(defaultValue = "5000000")
            @Min(1)
            @Max(PhoneService.MAX_LOAD_ITERATIONS)
            long iterations
    ) {
        return phoneService.createCpuLoad(iterations);
    }
}
