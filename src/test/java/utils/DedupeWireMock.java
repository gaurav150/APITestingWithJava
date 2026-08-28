package utils;

import java.io.IOException;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Permanent fix for WireMock stub duplication.
 *
 * What it does:
 * 1. Fetches ALL currently loaded stubs from a running WireMock instance
 *    (GET /__admin/mappings).
 * 2. Groups them by a "logical fingerprint" - method + url/urlPath/urlPathPattern
 *    + scenarioName + requiredScenarioState (ignores the random "id" field,
 *    which is exactly what makes duplicates hard to spot by eye, since a new
 *    UUID is generated every time the same content gets re-imported).
 * 3. Keeps exactly ONE stub per fingerprint (the first one seen).
 * 4. Resets the WireMock instance (clears everything, including duplicates).
 * 5. Re-imports ONLY the deduplicated set.
 * 6. Saves the clean set to clean-master-mappings.json - a single, permanent
 *    source of truth to use for every future import instead of an old file
 *    that already has duplicates baked into it.
 *
 * Usage (no external dependencies beyond Jackson, which you likely already
 * have via rest-assured's transitive deps - check your pom.xml first):
 *
 *   javac DedupeWireMock.java
 *   java DedupeWireMock
 *
 * Or pass a custom host:
 *   java DedupeWireMock http://localhost:8181
 */
public class DedupeWireMock {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "http://localhost:8181";
        boolean dryRun = Arrays.asList(args).contains("--dry-run");

        System.out.println("Fetching current mappings from " + host + " ...");
        JsonNode mappingsRoot = fetchMappings(host);
        ArrayNode mappings = (ArrayNode) mappingsRoot.get("mappings");
        System.out.println("Found " + mappings.size() + " stubs currently loaded.");

        LinkedHashMap<String, JsonNode> unique = new LinkedHashMap<>();
        List<String> removedIds = new ArrayList<>();

        for (JsonNode stub : mappings) {
            String fp = fingerprint(stub);
            if (unique.containsKey(fp)) {
                removedIds.add(stub.path("id").asText("(no id)"));
            } else {
                unique.put(fp, stub);
            }
        }

        System.out.println("Unique logical stubs: " + unique.size());
        System.out.println("Duplicate stubs identified: " + removedIds.size());
        if (!removedIds.isEmpty()) {
            System.out.println("Duplicate stub IDs that will be dropped:");
            removedIds.forEach(id -> System.out.println("  - " + id));
        }

        if (dryRun) {
            System.out.println("\nDry run - no changes made, no file written.");
            return;
        }

        if (removedIds.isEmpty()) {
            System.out.println("\nNo duplicates found. Nothing to do.");
            return;
        }

        System.out.println("\nResetting WireMock (clears all current stubs)...");
        resetWireMock(host);

        List<JsonNode> deduped = new ArrayList<>(unique.values());
        System.out.println("Re-importing " + deduped.size() + " deduplicated stubs...");
        importMappings(host, deduped);

        ObjectNode outRoot = MAPPER.createObjectNode();
        ArrayNode outMappings = outRoot.putArray("mappings");
        deduped.forEach(outMappings::add);
        ObjectNode meta = outRoot.putObject("meta");
        meta.put("total", deduped.size());

        String outFile = "clean-master-mappings.json";
        try (FileWriter writer = new FileWriter(outFile)) {
            writer.write(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(outRoot));
        }
        System.out.println("Clean, deduplicated set saved to: " + outFile);
        System.out.println("\nIMPORTANT: from now on, only ever import this file. Do not re-import");
        System.out.println("any older file that still contains the duplicates - that is what");
        System.out.println("caused the count to keep climbing back up every time.");
    }

    /**
     * Builds a fingerprint that identifies what a stub logically matches,
     * ignoring the random "id" field. Two stubs with the same fingerprint
     * are considered duplicates even if their ids differ.
     */
    private static String fingerprint(JsonNode stub) {
        JsonNode req = stub.path("request");
        StringBuilder sb = new StringBuilder();
        sb.append(req.path("method").asText(""));
        sb.append('|').append(req.path("url").asText(""));
        sb.append('|').append(req.path("urlPath").asText(""));
        sb.append('|').append(req.path("urlPathPattern").asText(""));
        sb.append('|').append(req.path("urlPattern").asText(""));
        sb.append('|').append(req.path("urlPathTemplate").asText(""));
        sb.append('|').append(req.has("bodyPatterns") ? req.get("bodyPatterns").toString() : "");
        sb.append('|').append(stub.path("scenarioName").asText(""));
        sb.append('|').append(stub.path("requiredScenarioState").asText(""));
        return sb.toString();
    }

    private static JsonNode fetchMappings(String host) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(host + "/__admin/mappings"))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return MAPPER.readTree(response.body());
    }

    private static void resetWireMock(String host) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(host + "/__admin/reset"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }

    private static void importMappings(String host, List<JsonNode> mappings) throws IOException, InterruptedException {
        ObjectNode root = MAPPER.createObjectNode();
        ArrayNode arr = root.putArray("mappings");
        mappings.forEach(arr::add);
        ObjectNode meta = root.putObject("meta");
        meta.put("total", mappings.size());

        String payload = MAPPER.writeValueAsString(root);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(host + "/__admin/mappings/import"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
