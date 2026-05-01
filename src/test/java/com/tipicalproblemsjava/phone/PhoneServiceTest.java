package com.tipicalproblemsjava.phone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PhoneServiceTest {

    @Autowired
    private PhoneService phoneService;

    @Test
    void createsAndFindsPhone() {
        PhoneResponse created = phoneService.create(new PhoneCreateRequest(
                "Apple",
                "iPhone 15",
                128,
                79900,
                true
        ));

        PhoneResponse found = phoneService.findById(created.id());

        assertThat(found.brand()).isEqualTo("Apple");
        assertThat(found.model()).isEqualTo("iPhone 15");
        assertThat(phoneService.findAll()).extracting(PhoneResponse::id).contains(created.id());
    }

    @Test
    void updatesAndDeletesPhone() {
        PhoneResponse created = phoneService.create(new PhoneCreateRequest(
                "Samsung",
                "Galaxy S24",
                256,
                89900,
                true
        ));

        PhoneResponse updated = phoneService.update(created.id(), new PhoneCreateRequest(
                "Samsung",
                "Galaxy S24 Ultra",
                512,
                119900,
                false
        ));
        phoneService.delete(created.id());

        assertThat(updated.model()).isEqualTo("Galaxy S24 Ultra");
        assertThat(updated.inStock()).isFalse();
        assertThatThrownBy(() -> phoneService.findById(created.id()))
                .isInstanceOf(PhoneNotFoundException.class);
    }

    @Test
    void reportsMissingPhone() {
        assertThatThrownBy(() -> phoneService.findById(100500L))
                .isInstanceOf(PhoneNotFoundException.class);
        assertThatThrownBy(() -> phoneService.update(100500L, new PhoneCreateRequest(
                "Missing",
                "Model",
                128,
                50000,
                true
        ))).isInstanceOf(PhoneNotFoundException.class);
        assertThatThrownBy(() -> phoneService.delete(100500L))
                .isInstanceOf(PhoneNotFoundException.class);
    }

    @Test
    void createsCpuLoadForPositiveIterations() {
        LoadResult result = phoneService.createCpuLoad(1_000);

        assertThat(result.iterations()).isEqualTo(1_000);
        assertThat(result.elapsedMillis()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void rejectsInvalidCpuLoadIterations() {
        assertThatThrownBy(() -> phoneService.createCpuLoad(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
        assertThatThrownBy(() -> phoneService.createCpuLoad(PhoneService.MAX_LOAD_ITERATIONS + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("less than or equal");
    }
}
