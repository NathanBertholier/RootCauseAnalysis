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
    private static String BODY = "";

    static {
        var values = new HashMap<String, String>() {
            {
                put("log", "2020-06-15	12:47:50	CDG50-C1	6542	82.255.169.186	GET     d2l739nh5t756g.cloudfront.net	/wp-includes/css/dist/block-library/style.min.css.gzip	200	https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/    Mozilla/5.0%20(Android%208.0.0;%20Mobile;%20rv:68.0)%20Gecputko/68.0%20Firefox/68.0	x97250&ver=5.3.3	-	Hit	axywzFh5wHuHwTX14cKd2NiDu95YLfUvPhDhzy1XDMS6-sTTT-RNRw==	static.centreon.com	https	281	0.003	-	TLSv1.2	ECDHE-RSA-AES128-GCM-SHA256	Hit	HTTP/2.0	-	-	52780	0.003	Hit	text/css	6163	-	-");
            }
        };
        List<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 7450; i++) {

            list.add(values);
        }
        var objectMapper = new ObjectMapper();
        try {
            BODY = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Benchmark() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    public void sendPost(URI uri) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(BODY))
                .header("Accept", "application/json").header("Content-Type", "application/json").build();
        HttpResponse<String> send = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        //System.out.println(send.body());
    }

    public void sendGet(URI uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::println).join();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Benchmark benchmark = new Benchmark();
        benchmark.sendPost(URI.create("http://localhost:80/external/insertlog/batch"));
    }
}
