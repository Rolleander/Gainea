package org.slf4j;

import com.google.gwt.core.client.GWT;

/**
 * Lightweight SLF4J replacement used only by the GWT compiler.
 */
public final class LoggerFactory {
    private LoggerFactory() {
    }

    public static Logger getLogger(Class<?> type) {
        return new ConsoleLogger(type == null ? "unknown" : type.getName());
    }

    private static final class ConsoleLogger implements Logger {
        private final String name;

        private ConsoleLogger(String name) {
            this.name = name;
        }

        @Override
        public void info(String message) {
            GWT.log(format("INFO", message));
        }

        @Override
        public void error(String message) {
            GWT.log(format("ERROR", message));
        }

        @Override
        public void error(String message, Throwable error) {
            GWT.log(format("ERROR", message), error);
        }

        private String format(String level, String message) {
            return "[" + level + "] [" + name + "] " + message;
        }
    }
}
