package com.tipicalproblemsjava.phone;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhoneService {

    static final long MAX_LOAD_ITERATIONS = 1_000_000_000L;

    private static final Logger log = LoggerFactory.getLogger(PhoneService.class);

    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Transactional(readOnly = true)
    public List<PhoneResponse> findAll() {
        log.trace("Loading all phones from repository");
        List<PhoneResponse> phones = phoneRepository.findAll().stream()
                .map(PhoneResponse::from)
                .toList();
        log.debug("Loaded {} phones", phones.size());
        return phones;
    }

    @Transactional(readOnly = true)
    public PhoneResponse findById(Long id) {
        log.trace("Loading phone by id={}", id);
        return phoneRepository.findById(id)
                .map(PhoneResponse::from)
                .orElseThrow(() -> {
                    log.warn("Phone id={} was requested but does not exist", id);
                    return new PhoneNotFoundException(id);
                });
    }

    @Transactional
    public PhoneResponse create(PhoneCreateRequest request) {
        log.debug("Creating phone brand='{}', model='{}'", request.brand(), request.model());
        Phone saved = phoneRepository.save(new Phone(
                request.brand(),
                request.model(),
                request.storageGb(),
                request.price(),
                request.inStock()
        ));
        log.info("Created phone id={}", saved.getId());
        return PhoneResponse.from(saved);
    }

    @Transactional
    public PhoneResponse update(Long id, PhoneCreateRequest request) {
        log.debug("Updating phone id={}", id);
        Phone phone = phoneRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update missing phone id={}", id);
                    return new PhoneNotFoundException(id);
                });

        phone.update(request.brand(), request.model(), request.storageGb(), request.price(), request.inStock());
        log.info("Updated phone id={}", id);
        return PhoneResponse.from(phone);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting phone id={}", id);
        if (!phoneRepository.existsById(id)) {
            log.warn("Cannot delete missing phone id={}", id);
            throw new PhoneNotFoundException(id);
        }

        phoneRepository.deleteById(id);
        log.info("Deleted phone id={}", id);
    }

    public LoadResult createCpuLoad(long iterations) {
        validateIterations(iterations);
        log.warn("Starting intentional CPU load, iterations={}", iterations);
        log.trace("CPU load maxIterations={}", MAX_LOAD_ITERATIONS);

        long startedAt = System.nanoTime();
        long checksum = 0;
        for (long index = 0; index < iterations; index++) {
            checksum += ((index * 31) ^ (iterations - index)) % 97;
            checksum ^= checksum << 7;
            checksum ^= checksum >>> 11;
        }

        long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        log.info("Finished intentional CPU load, iterations={}, elapsedMs={}", iterations, elapsedMillis);
        return new LoadResult(iterations, checksum, elapsedMillis);
    }

    private void validateIterations(long iterations) {
        if (iterations <= 0) {
            log.error("Rejected CPU load request because iterations={} is not positive", iterations);
            throw new IllegalArgumentException("iterations must be positive");
        }
        if (iterations > MAX_LOAD_ITERATIONS) {
            log.error("Rejected CPU load request because iterations={} exceeds {}", iterations, MAX_LOAD_ITERATIONS);
            throw new IllegalArgumentException("iterations must be less than or equal to " + MAX_LOAD_ITERATIONS);
        }
    }
}
