package org.slf4j;

/**
 * Browser-side subset of SLF4J used by the Gainea client.
 */
public interface Logger {
    void info(String message);

    void error(String message);

    void error(String message, Throwable error);
}
