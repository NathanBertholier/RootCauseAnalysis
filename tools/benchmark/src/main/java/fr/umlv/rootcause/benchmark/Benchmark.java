package fr.umlv.rootcause.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Benchmark {
    private final HttpClient httpClient;

    public Benchmark() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    public void sendPost(URI uri) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(prepareRequest()))
                .header("Accept", "application/json").header("Content-Type","application/json").build();
        HttpResponse<String> send = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(send.statusCode());

    }

    private String prepareRequest() throws JsonProcessingException {
        var values = new HashMap<String, String>() {
            {
                put("log", "2021-11-20 00:00:01\t10.16.27.62.244\tGET\tindex.html");
            }
        };
        List<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(values);
        }
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(list);
    }

    public void sendGet(URI uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::println).join();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Benchmark benchmark = new Benchmark();
        //benchmark.sendGet(URI.create("http://localhost:8080/external/tokentypes"));
        while (true) {
            benchmark.sendPost(URI.create("http://localhost:80/external/insertlog/batch"));
            Thread.sleep(500);
        }
    }
}
