package com.microsoft.identity.client.ui.automation.logging;

import androidx.annotation.NonNull;

public class Logger {

    /**
     * Send a {@link LogLevel#ERROR} log message without PII.
     *
     * @param tag     Used to identify the source of a log message.
     *                It usually identifies the class or activity where the log call occurs.
     * @param message The error message to log.
     */
    public static void e(@NonNull final String tag,
                         @NonNull final String message) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.e(tag, message);
        }
    }

    /**
     * Send a {@link LogLevel#ERROR} log message without PII.
     *
     * @param tag       Used to identify the source of a log message.
     *                  It usually identifies the class or activity where the log call occurs.
     * @param message   The error message to log.
     * @param exception An exception to log
     */
    public static void e(@NonNull final String tag,
                         @NonNull final String message,
                         @NonNull final Throwable exception) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.e(tag, message, exception);
        }
    }


    /**
     * Send a {@link LogLevel#WARN} log message without PII.
     *
     * @param tag     Used to identify the source of a log message.
     *                It usually identifies the class or activity where the log call occurs.
     * @param message The error message to log.
     */
    public static void w(@NonNull final String tag,
                         @NonNull final String message) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.w(tag, message);
        }
    }

    /**
     * Send a {@link LogLevel#WARN} log message without PII.
     *
     * @param tag       Used to identify the source of a log message.
     *                  It usually identifies the class or activity where the log call occurs.
     * @param message   The error message to log.
     * @param exception An exception to log
     */
    public static void w(@NonNull final String tag,
                         @NonNull final String message,
                         @NonNull final Throwable exception) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.w(tag, message, exception);
        }
    }

    /**
     * Send a {@link LogLevel#INFO} log message without PII.
     *
     * @param tag     Used to identify the source of a log message.
     *                It usually identifies the class or activity where the log call occurs.
     * @param message The error message to log.
     */
    public static void i(@NonNull final String tag,
                         @NonNull final String message) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.i(tag, message);
        }
    }

    /**
     * Send a {@link LogLevel#INFO} log message without PII.
     *
     * @param tag       Used to identify the source of a log message.
     *                  It usually identifies the class or activity where the log call occurs.
     * @param message   The error message to log.
     * @param exception An exception to log
     */
    public static void i(@NonNull final String tag,
                         @NonNull final String message,
                         @NonNull final Throwable exception) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.i(tag, message, exception);
        }
    }


    /**
     * Send a {@link LogLevel#VERBOSE} log message without PII.
     *
     * @param tag     Used to identify the source of a log message.
     *                It usually identifies the class or activity where the log call occurs.
     * @param message The error message to log.
     */
    public static void v(@NonNull final String tag,
                         @NonNull final String message) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.v(tag, message);
        }
    }

    /**
     * Send a {@link LogLevel#VERBOSE} log message without PII.
     *
     * @param tag       Used to identify the source of a log message.
     *                  It usually identifies the class or activity where the log call occurs.
     * @param message   The error message to log.
     * @param exception An exception to log
     */
    public static void v(@NonNull final String tag,
                         @NonNull final String message,
                         @NonNull final Throwable exception) {
        for (ILogger logger : LoggerRegistry.getRegisteredLoggers()) {
            logger.v(tag, message, exception);
        }
    }
}
