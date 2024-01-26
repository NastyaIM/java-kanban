package model;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Const {
    public static final Duration DEFAULT_DURATION = Duration.ZERO;
    public static final LocalDateTime DEFAULT_START_TIME =
            LocalDateTime.of(2100, 1, 1, 0, 0);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
}
