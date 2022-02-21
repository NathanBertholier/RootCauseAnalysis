package fr.umlv.rootcause.benchmark;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Benchmark {

    public static void main(String[] args) throws ParseException, IOException, InterruptedException {
        //use this to try out spamming rabbitmq
        //args = new String[]{"-l", "100000", "-F", "../logs/", "-b", "50", "-d", "85", "-t", "8", "-u", "http://localhost:80/external/insertlog/batch", "-nT", "2022-02-18 11:57:00"};
        //args = new String[]{"-l", "100000", "-F", "../logs/", "-b", "50", "-d", "85", "-t", "8", "-u", "http://localhost:80/external/insertlog/batch"};

        //use this to output to out.txt in exec folder
        //args = new String[]{"-l", "10000", "-f", "in.txt"};

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
        Timestamp ts = null;
        if(line.hasOption("newTimeStamp")) {
            ts = Timestamp.valueOf(line.getOptionValue("newTimeStamp"));
        }
        String URITarget = line.getOptionValue("uriTarget");
        sendToServer(stringList, Integer.parseInt(line.getOptionValue("threadCount")), Integer.parseInt(line.getOptionValue("batchSize")),Integer.parseInt(line.getOptionValue("delay")), ts, URITarget);
    }

    private static void sendToServer(List<String> stringSource, int threadCount, int batchSize, int millisDelay, Timestamp ts, String URITarget) throws InterruptedException {

        Thread[] threads = new Thread[threadCount];
        ArrayList<List<String>> subLists = new ArrayList<>();
        for (int i = 0; i < threadCount; i++){
            int offset = stringSource.size()/threadCount;
            subLists.add(stringSource.subList(offset*i,offset*(i+1)));
            int finalI = i;
            threads[i] = new Thread(() -> {
                BenchHTTPClient client = new BenchHTTPClient(batchSize, ts, URITarget);
                client.sendToServer(subLists.get(finalI),millisDelay);
            });
        }

        for(Thread t : threads) {
            t.start();
            Thread.sleep(millisDelay/threadCount);
        }

        for(Thread t : threads) {
            t.join();
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

        final Option outputOption = Option.builder("o").longOpt("output").desc("path of Output File if output needed (automatically overwrites, be careful !), will be out.txt if left blank").hasArg(true).argName("outputPath").required(false).build();

        final Option batchSizeOption = Option.builder("b").longOpt("batchSize").desc("number of lines to send in each HTTP request to server").hasArg(true).argName("batchSize").required(true).build();

        final Option delayOption = Option.builder("d").longOpt("delay").desc("delay between each HTTP request (each thread uses this delay, so for example with 8 threads and a 100ms delay there will be 80 requests per second").hasArg(true).argName("delay").required(true).build();

        final Option threadCountOption = Option.builder("t").longOpt("threadCount").desc("number of threads to use as HTTP senders, this doesn't change the behavior or performance of the rest of the program").hasArg(true).argName("delay").required(true).build();

        final Option newTimeStampOption = Option.builder("nT").longOpt("newTimeStamp").desc("Replace read time stamps with current time stamp").hasArg(true).argName("newTimeStamp").required(false).build();

        final Option URITargetOption = Option.builder("u").longOpt("uriTarget").desc("Replace read time stamps with current time stamp").hasArg(true).argName("uriTarget").required(true).build();

        final Options options = new Options();

        for (final Option fo : firstOptions.getOptions()) {
            options.addOption(fo);
        }

        options.addOption(filePathOption);
        options.addOption(folderPathOption);
        options.addOption(linesOption);
        options.addOption(outputOption);
        options.addOption(batchSizeOption);
        options.addOption(delayOption);
        options.addOption(threadCountOption);
        options.addOption(newTimeStampOption);
        options.addOption(URITargetOption);

        return options;
    }
}

class BenchHTTPClient{

    private final HttpClient httpClient;
    private final int batchSize;
    private final boolean modifiedTimestamp;
    private final Timestamp ts;
    private final long creation;
    private final URI sendURI;

    public BenchHTTPClient(int batchSize, Timestamp ts, String URITarget) {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();
        this.batchSize = batchSize;
        this.sendURI = URI.create(URITarget);
        creation = Timestamp.from(Instant.now()).getTime();
        if(ts != null){
            modifiedTimestamp = true;
            this.ts = ts;
        }
        else{
            this.ts = null;
            modifiedTimestamp = false;
        }
    }

    private long getTimeOffset() {
        return Timestamp.from(Instant.now()).getTime() - creation;
    }

    public String timeStampToString(Timestamp t) {
        String str = t.toString().replace(" ","\\t");
        return str.substring(0,str.indexOf('.'));
    }

    public void sendPost(URI uri, List<String> stringList) throws IOException, InterruptedException {
        String str = prepareRequest(stringList);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(str))
                .header("Accept", "application/json").header("Content-Type", "application/json").build();
        HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if(send.statusCode() != 200) {
            System.out.println(send.statusCode());
        }
    }

    private String prepareRequest(List<String> stringList) {
        String prefix = "[{\"log\":\"";
        String separator = "\"},{\"log\":\"";
        String postfix = "\"}]";
        StringBuilder sb = new StringBuilder();

        sb.append(prefix);
        for (String s : stringList){
            if(modifiedTimestamp) {
                sb.append(timeStampToString(new Timestamp(ts.getTime() + getTimeOffset())));
                sb.append(s.substring(19).replace("\t","\\t"));
            }
            else {
                sb.append(s.replace("\t","\\t"));
            }
            sb.append(separator);
        }
        sb.setLength(sb.length() - separator.length());
        sb.append(postfix);
        return sb.toString();
    }

    public void sendToServer(List<String> stringList, int millisDelay) {
            try {
                if(stringList.size() > batchSize) {
                    for(List<String> list : cutList(stringList,batchSize)) {
                        this.sendPost(sendURI,list);
                        Thread.sleep(millisDelay);
                    }
                }
                else {
                    this.sendPost(sendURI,stringList);
                    Thread.sleep(millisDelay);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
    }
    private List<List<String>> cutList(List<String> originalList, int partitionSize) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i += partitionSize) {
            partitions.add(originalList.subList(i,
                    Math.min(i + partitionSize, originalList.size())));
        }
        return partitions;
    }
}
