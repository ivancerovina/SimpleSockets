package me.ivancerovina.simplesockets.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Logger { // TODO Implement a logging library
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH/mm/ss");
    private final String name;

    public Logger(String name)  {
        this.name = name;
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Throwable throwable) {
        message("INFO", message, ConsoleColor.BLUE, throwable);
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void warn(String message, Throwable throwable) {
        message("WARN", message, ConsoleColor.YELLOW, throwable);
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable throwable) {
        message("ERROR", message, ConsoleColor.RED, throwable);
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable throwable) {
        message("DEBUG", message, ConsoleColor.YELLOW_BOLD, throwable);
    }

    private void message(String level, String message, ConsoleColor color, Throwable throwable) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(color, "color");

        String formattedMessage = String.format("[%s] [%s %s] %s", dateFormatter.format(LocalDateTime.now()), name, level, message);

        System.out.print(color);
        System.out.print(formattedMessage);

        if (throwable != null) {
            System.out.println();
            System.out.println(throwable);
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (var trace : stackTrace) {
                System.out.println(trace);
            }
        }

        System.out.println(ConsoleColor.RESET);
    }

    private enum ConsoleColor {
        RESET("\033[0m"),
        YELLOW("\033[0;33m"),
        BLUE("\033[0;34m"),
        YELLOW_BOLD("\033[1;33m"),
        RED("\033[0;91m");

        private final String code;

        ConsoleColor(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

}
