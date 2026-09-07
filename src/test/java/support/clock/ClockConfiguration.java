package support.clock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@TestConfiguration
public class ClockConfiguration {
    @Bean
    @Primary
    public Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-08-25T03:00:00Z"),
                ZoneOffset.UTC);
    }
}
