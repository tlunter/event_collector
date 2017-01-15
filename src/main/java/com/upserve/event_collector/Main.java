package com.upserve.event_collector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.upserve.event_collector.config.Config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    private final URL url;

    public static final int EXIT_CODE_USAGE = 2;
    public static final int EXIT_CODE_BAD_URL = 3;
    public static final int EXIT_CODE_UNEXPECTED_FAIL = 4;

    public Main(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("null url");
        }

        this.url = url;
    }

    public static void usage() {
        System.err.println("usage: java event_collector.jar <config-url>");
    }

    public int run() throws Exception {
        log.info("Starting event_collector");
        try (Engine engine = fetchConfig()) {
            log.info("Starting Engine: {}", engine);
            engine.run();
        }

        return 0;
    }

    public static void main(String... args) throws Exception {
        int status;
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                System.err.println("[" + Instant.now().toString() + "] uncaught exception in thread: " + t.getName());
                e.printStackTrace(System.err);
                System.exit(EXIT_CODE_UNEXPECTED_FAIL);
            });

            if (args.length != 1) {
                usage();
                status = EXIT_CODE_USAGE;
            } else {
                URL url = new URL(args[0]);
                Main main = new Main(url);

                status = main.run();
            }
        } catch (MalformedURLException e) {
            System.err.println("Illegal config-url: " + e.getMessage());
            status = EXIT_CODE_BAD_URL;
        } catch (Throwable t) {
            log.error("Unexpected Failure", t);
            status = EXIT_CODE_UNEXPECTED_FAIL;
        }

        System.exit(status);
    }

    private Engine fetchConfig() {
        log.info("Fetching: {}", url);
        try (InputStream in = url.openStream()) {
            String ystr = loadMergedYaml(in);
            for (Map.Entry<String,String> entry : System.getenv().entrySet()) {
                ystr = ystr.replace("${" + entry.getKey() + "}", entry.getValue());
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
            Config config = mapper.readValue(ystr, Config.class);

            log.info("Fetched config: {}", config);
            return new Engine(config);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Exposing SnakeYAML implementation to support merging
     *
     * https://github.com/FasterXML/jackson-dataformat-yaml/issues/20
     */
    @Deprecated
    private String loadMergedYaml(InputStream stream) {
        Yaml yaml = new Yaml();
        return yaml.dump(yaml.load(stream));
    }

    private static synchronized void consoleLog(String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss,SSS").format(new Date());
        System.err.printf("%s %s\n", date, message);
    }
}
