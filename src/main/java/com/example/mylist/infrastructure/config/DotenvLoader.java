package com.example.mylist.infrastructure.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DotenvLoader {

    private static final Path DOTENV_PATH = Path.of(".env");

    private DotenvLoader() {
    }

    public static void loadIntoSystemProperties() {
        if (!Files.exists(DOTENV_PATH)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(DOTENV_PATH);
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int separator = line.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String key = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                if (key.isEmpty() || value.isEmpty()) {
                    continue;
                }

                if (System.getenv(key) == null && System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException ignored) {
            // Ignore local .env loading issues and let standard configuration resolution continue.
        }
    }
}
