package dev.dkaz.vidme.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TimeUtils {
    public String getElapsedTime(Instant t1, Instant t2) {
        if (t1.isAfter(t2)) throw new IllegalArgumentException("t1 cannot be after t2");
        Duration duration = Duration.between(t1, t2);

        long days = duration.toDaysPart();
        if (days > 0) return String.format("%s day%s", days, days > 1 ? "s" : "");

        long hours = duration.toHoursPart();
        if (hours > 0) return String.format("%s hour%s", hours, hours > 1 ? "s" : "");

        long minutes = duration.toMinutesPart();
        return String.format("%s minute%s", minutes, minutes > 1 ? "s" : "");
    }
}
