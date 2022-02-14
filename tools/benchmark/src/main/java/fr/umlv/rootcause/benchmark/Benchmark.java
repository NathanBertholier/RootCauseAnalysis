package fr.umlv.rootcause.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Benchmark {

    public static void main(String[] args) throws ParseException, IOException {
        //args = new String[]{"-l", "10000000", "-F", "../logs/"};
        args = new String[]{"-l", "10000", "-f", "in.txt"};
        final Options firstOptions = configFirstParameters();
        final Options options = configParameters(firstOptions);

        final CommandLineParser parser = new DefaultParser();
        final CommandLine firstLine = parser.parse(firstOptions, args, true);

        if (firstLine.hasOption("help")) {
            printHelp(options);
        }

        CommandLine line = null;
        try {
            line = parser.parse(options, args, true);
        } catch (MissingOptionException e) {
            System.err.println(e.getMessage());
            printHelp(options);
        }
        Stream<String> stream;
        assert line != null;
        if (!line.hasOption("file")) {
            if (!line.hasOption("folder")) {
                System.err.println("No folder or file has been supplied as argument, one of the two is needed");
                printHelp(options);
            }
            stream = getFilesFromFolder(Paths.get(line.getOptionValue("folder")), lineLimit(line));
        } else {
            stream = getStreamFromFile(Paths.get(line.getOptionValue("file")));
        }
        int lineCount = Integer.parseInt(line.getOptionValue("linesCount"));

        List<String> stringList = stream
                .limit(lineLimit(line))
                .collect(Collectors.toList());
        try {
            Path outputPath;
            if (line.hasOption("output")) {
                outputPath = Path.of(line.getOptionValue("output"));
            } else {
                outputPath = Path.of("out.txt");
            }
            System.out.println("Writing to file...");
            Files.write(outputPath,stringList);
        } catch (NoSuchFileException e) {
            System.err.println("File not found, check file path : " + e.getMessage());
        }
        var logSender = new BenchHTTPClient();
        //sendToServer(stringList, 8, false);
        logSender.sendToServer();
    }

    private static void sendToServer(List<String> stringSource, int threadCount, boolean looping) {

        Thread[] threads = new Thread[threadCount];
        ArrayList<List<String>> subLists = new ArrayList<>();
        for (int i = 0; i < threadCount; i++){
            int offset = stringSource.size()/threadCount;
            subLists.add(stringSource.subList(offset*i,offset*(i+1)));
            threads[i] = new Thread(() -> {
                BenchHTTPClient client = new BenchHTTPClient();
                client.sendToServer();
            });
        }
    }

    private static Long lineLimit(CommandLine line) {
        return line.hasOption("linesCount") ? Long.parseLong(line.getOptionValue("linesCount")) : Long.MAX_VALUE;
    }

    private static Stream<String> getFilesFromFolder(Path folderPath, long limit) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        DirectoryStream<Path> dir = Files.newDirectoryStream(folderPath);
        AtomicLong atomicLong = new AtomicLong();
        for (Path path : dir) {
            Stream<String> stream = getStreamFromFile(path);
            stream.forEach(x -> {
                atomicLong.incrementAndGet();
                synchronized (list) {
                    list.add(x);
                }
            });
            long l = atomicLong.get();
            if (l % 250 == 0) {
                System.out.println(Math.round((float) l / limit * 1000.0) / 10.0 + " % complete");
            }
            if (l >= limit) {
                break;
            }
        }
        return list.stream();
    }

    private static Stream<String> getStreamFromFile(Path filePath) throws IOException {
        return Files.lines(filePath).skip(2);
    }

    private static void printHelp(Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Benchmark", options, true);
        System.exit(0);
    }

    private static Options configFirstParameters() {

        final Option helpFileOption = Option.builder("h").longOpt("help").desc("Shows help message").build();

        final Options firstOptions = new Options();

        firstOptions.addOption(helpFileOption);

        return firstOptions;
    }

    private static Options configParameters(final Options firstOptions) {

        final Option filePathOption = Option.builder("f").longOpt("file") //
                .desc("File to read").hasArg(true).argName("filePath").required(false).build();

        final Option folderPathOption = Option.builder("F").longOpt("folder").desc("Folder to read, in this mode only logs starting with \"EOX\" will be read").hasArg(true).argName("folderPath").required(false).build();

        final Option linesOption = Option.builder("l").longOpt("linesCount").desc("max number of lines to keep").hasArg(true).argName("lines").required(false).build();

        final Option outputOption = Option.builder("o").longOpt("output").desc("path of Output File (automatically overwrites, be careful !), will be out.txt if left blank").hasArg(true).argName("outputPath").required(false).build();

        final Options options = new Options();

        for (final Option fo : firstOptions.getOptions()) {
            options.addOption(fo);
        }
        options.addOption(filePathOption);
        options.addOption(folderPathOption);
        options.addOption(linesOption);
        options.addOption(outputOption);

        return options;
    }


}

class BenchHTTPClient{

    private final HttpClient httpClient;

    public BenchHTTPClient() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    public void sendGet(URI uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::println).join();
    }

    public void sendPost(URI uri) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(prepareRequest()))
                .header("Accept", "application/json").header("Content-Type", "application/json").build();
        HttpResponse<String> send = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(send.statusCode());
    }

    private String prepareRequest() throws JsonProcessingException {
        var values = new HashMap<String, String>() {
            {
                put("log", "2020-08-25\t12:20:19\tLHR62-C4\t997\t88.217.152.130\tGET\td2l739nh5t756g.cloudfront.net\t/wp-content/plugins/easy-twitter-feed-widget/js/twitter-widgets.js\t200\t-\tMozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/83.0.4103.61%20Safari/537.36\tx56322&ver=1.0\t-\tHit\terS8nLIX1EwugGs0W_f3E-tu4O7noesqVdNE49TGeRQ5PdgbLUJ4Ig==\tstatic.centreon.com\thttps\t354\t0.001\t-\tTLSv1.2\tECDHE-RSA-AES128-GCM-SHA256\tHit\tHTTP/1.1\t-\t-\t37457\t0.001\tHit\tapplication/x-javascript\t486\t-\t-\n");
            }
        };
        List<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(values);
        }
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(list);
    }

    public void sendToServer(){
        this.sendGet(URI.create("http://localhost:80/external/tokentypes"));
        while (true) {
            try {
                this.sendPost(URI.create("http://localhost:80/external/insertlog/batch"));
                Thread.sleep(500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}