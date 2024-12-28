package helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.asynchttpclient.*;
import java.util.concurrent.CompletableFuture;

public class TranslationHelper {
    Properties properties = new Properties();
    String from = "es";
    String to = "en";

    public CompletableFuture<String> translateText(String text) throws IOException {
        FileInputStream fis = new FileInputStream("src/resources/settings.properties");
        properties.load(fis);
        String rapidApiKey = properties.getProperty("rapidApiKey");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = String.format("{\"from\":\"es\",\"to\":\"en\",\"q\":\"%s\"}", text);
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        return client.prepare("POST", "https://rapid-translate-multi-traduction.p.rapidapi.com/t")
                .setHeader("x-rapidapi-key", rapidApiKey)
                .setHeader("x-rapidapi-host", "rapid-translate-multi-traduction.p.rapidapi.com")
                .setHeader("Content-Type", "application/json")
                .setBody(requestBody)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    // Parse JSON response into a String
                    String[] translations = new String[0];
                    try {
                        translations = objectMapper.readValue(response.getResponseBody(), String[].class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    // Return the first string from the array
                    return translations[0];
                })
                .whenComplete((result, throwable) -> {
                    // Close the client after the response is processed
                    try {
                        client.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}


