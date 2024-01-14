package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Const {
    public static final Duration defaultDuration = Duration.ZERO;
    public static final LocalDateTime defaultStartTime =
            LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
}
