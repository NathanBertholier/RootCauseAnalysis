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
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Benchmark {
    private static final Logger LOGGER = Logger.getGlobal();
    private static final String FOLDER = "folder";
    private static final String OUTPUT = "output";
    private static final String NEW_TIME_STAMP = "newTimeStamp";
    private static final String BATCH_SIZE = "batchSize";
    private static final String LINES_COUNT = "linesCount";
    private static final String DELAY = "delay";
    private static final String THREAD_COUNT = "threadCount";
    private static final String URI_TARGET = "uriTarget";
    private static final String LOOP_ON_DATA = "loopOnData";
    private static final String STAT_PATH = "statPath";

    public static void main(String[] args) throws ParseException, IOException, InterruptedException {
        //use this to try out spamming rabbitmq with different parameters
        //args = new String[]{"-l", "100000", "-F", "../logs/", "-b", "50", "-d", "85", "-t", "8", "-u", "http://localhost:8081/insertlog"};
        args = new String[]{"-l", "1000", "-F", "../logs/", "-b", "50", "-d", "85", "-t", "4", "-u", "http://localhost:8081/insertlog","-s","stats.csv"};



        //use this to output to out.txt in exec folder

        /*generate command line argument reader*/
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
            LOGGER.log(Level.SEVERE, e.getMessage());
            printHelp(options);
        }

        /*Try to read log files with the path passed as argument*/
        Stream<String> stream;
        assert line != null;
        if (!line.hasOption("file")) {
            if (!line.hasOption(FOLDER)) {
                LOGGER.log(Level.SEVERE,"No folder or file has been supplied as argument, one of the two is needed" );
                printHelp(options);
            }
            stream = getFilesFromFolder(Paths.get(line.getOptionValue(FOLDER)), lineLimit(line));
        } else {
            stream = getStreamFromFile(Paths.get(line.getOptionValue("file")));
        }

        List<String> stringList = stream
                .limit(lineLimit(line))
                .toList();
        try {
            Path outputPath;
            if (line.hasOption(OUTPUT)) {
                outputPath = Path.of(line.getOptionValue(OUTPUT));
            } else {
                outputPath = Path.of("out.txt");
            }
            LOGGER.log(Level.INFO, "Writing to file...");
            Files.write(outputPath,stringList);
        } catch (NoSuchFileException e) {
            LOGGER.log(Level.SEVERE, "File not found, check file path : ", e);
        }
        Timestamp ts = null;
        if(line.hasOption(NEW_TIME_STAMP)) {
            ts = Timestamp.valueOf(line.getOptionValue(NEW_TIME_STAMP));
        }
        String uriTarget = line.getOptionValue(URI_TARGET);
        int threadCount = Integer.parseInt(line.getOptionValue(THREAD_COUNT));
        int batchSize = Integer.parseInt(line.getOptionValue(BATCH_SIZE));
        int delay = Integer.parseInt(line.getOptionValue(DELAY));
        boolean loopOnData = line.hasOption(LOOP_ON_DATA);

        Path statPath;
        if (line.hasOption(STAT_PATH)) {
            statPath = Path.of(line.getOptionValue(STAT_PATH));
        } else {
            statPath = Path.of("stat.csv");
        }

        sendToServer(stringList, threadCount, batchSize, delay, ts, uriTarget, loopOnData, statPath);
    }

    /*Main functioning function : launches the different threads and sets them up with the arguments provided at program launch*/
    private static void sendToServer(List<String> stringSource, int threadCount, int batchSize, int millisDelay, Timestamp ts, String uriTarget, boolean loop, Path statPath) throws InterruptedException {
        Thread[] threads = new Thread[threadCount];
        ArrayList<List<String>> subLists = new ArrayList<>();

        for (int i = 0; i < threadCount; i++){
            int offset = stringSource.size()/threadCount;
            subLists.add(stringSource.subList(offset*i,offset*(i+1)));
            int finalI = i;
            threads[i] = new Thread(() -> {
                BenchHTTPClient client;
                if(finalI == 0){
                    client = new BenchHTTPClient(batchSize, ts, uriTarget, statPath);
                } else {
                    client = new BenchHTTPClient(batchSize, ts, uriTarget, null);
                }
                client.sendToServer(subLists.get(finalI), millisDelay, loop);
            });
        }

        /*Separates the threads starting over time to have a more uniform load on the server*/
        for(Thread t : threads) {
            t.start();
            Thread.sleep(millisDelay/threadCount);
        }


        for(Thread t : threads) {
            t.join();
        }
    }

    /**
     * Helper function that returns the limit of lines to be read from the path passed as argument
     * @return a Long integer containing the max number of lines to read
     */
    private static Long lineLimit(CommandLine line) {
        return line.hasOption(LINES_COUNT) ? Long.parseLong(line.getOptionValue(LINES_COUNT)) : Long.MAX_VALUE;
    }

    /**
     * Reads the path passed as argument, separates the different logs, reformats them according to specification and returns them in a stream of String
     * @return a stream of formatted strings, not bigger than the limit provided, each containing a log read from the path passed as argument.
     */
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
                LOGGER.log(Level.INFO, () -> Math.round((float) l / limit * 1000.0) / 10.0 + " % complete");
            }
            if (l >= limit) {
                break;
            }
        }
        return list.stream();
    }

    /**
     * Helper function that reads the raw log and returns a stream of String, skipping the two first lines (headers)
     */
    private static Stream<String> getStreamFromFile(Path filePath) throws IOException {
        return Files.lines(filePath).skip(2);
    }

    /**
     * Prints help summary to standard output
     */
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

    /**
     * Creates and returns the Options representing allowed arguments for the program
     */
    private static Options configParameters(final Options firstOptions) {

        final Option filePathOption = Option.builder("f").longOpt("file") //
                .desc("File to read").hasArg(true).argName("filePath").required(false).build();

        final Option folderPathOption = Option.builder("F").longOpt(FOLDER).desc("Folder to read, in this mode only logs starting with \"EOX\" will be read").hasArg(true).argName("folderPath").required(false).build();

        final Option linesOption = Option.builder("l").longOpt(LINES_COUNT).desc("Max number of lines to keep").hasArg(true).argName("lines").required(false).build();

        final Option outputOption = Option.builder("o").longOpt(OUTPUT).desc("Path of Output File if output needed (automatically overwrites, be careful !), will be out.txt if left blank").hasArg(true).argName("outputPath").required(false).build();

        final Option batchSizeOption = Option.builder("b").longOpt(BATCH_SIZE).desc("Number of lines to send in each HTTP request to server").hasArg(true).argName(BATCH_SIZE).required(true).build();

        final Option delayOption = Option.builder("d").longOpt(DELAY).desc("Delay between each HTTP request (each thread uses this delay, so for example with 8 threads and a 100ms delay there will be 80 requests per second").hasArg(true).argName(DELAY).required(true).build();

        final Option threadCountOption = Option.builder("t").longOpt(THREAD_COUNT).desc("Number of threads to use as HTTP senders, this doesn't change the behavior or performance of the rest of the program").hasArg(true).argName(THREAD_COUNT).required(true).build();

        final Option newTimeStampOption = Option.builder("nT").longOpt(NEW_TIME_STAMP).desc("Replace read time stamps with current time stamp").hasArg(true).argName(NEW_TIME_STAMP).required(false).build();

        final Option uriTargetOption = Option.builder("u").longOpt(URI_TARGET).desc("Indicate target URI to send the HTTP requests to").hasArg(true).argName(URI_TARGET).required(true).build();

        final Option loopOption = Option.builder("lo").longOpt(LOOP_ON_DATA).desc("Using this option will make the sender loop on the data loaded, instead of shutting down after sending all lines read").hasArg(false).required(false).build();

        final Option statOption = Option.builder("s").longOpt(STAT_PATH).desc("Using this option will activate HTTP response time logging, a path for the stat file will be required as well. The application will update the file every 10 seconds and statistics are only collected on thread 0").required(true).hasArg(true).required(false).build();

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
        options.addOption(uriTargetOption);
        options.addOption(loopOption);
        options.addOption(statOption);

        return options;
    }
}

/**
 * HTTP Client designed to upload logs provided to it to the server, according to its set parameters.
 */
class BenchHTTPClient{
    private static final Logger LOGGER = Logger.getGlobal();

    private final Map<Integer,List<Long>> responseTimes;
    private final HttpClient httpClient;
    private final int batchSize;
    private final boolean modifiedTimestamp;
    private final Timestamp ts;
    private final long creation;
    private final URI sendURI;
    private final Path statPath;
    private final Thread statThread;

    public BenchHTTPClient(int batchSize, Timestamp ts, String uriTarget, Path statPath) {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).build();
        this.batchSize = batchSize;
        this.sendURI = URI.create(uriTarget);
        this.creation = Timestamp.from(Instant.now()).getTime();

        this.statPath = statPath;
        if(ts != null){
            modifiedTimestamp = true;
            this.ts = ts;
        } else {
            this.ts = null;
            modifiedTimestamp = false;
        }
        if(statPath != null) {
            responseTimes = Collections.synchronizedMap(new HashMap<>());
            this.statThread = new Thread( () -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(5000);
                        StringBuilder builder = new StringBuilder();
                        synchronized (responseTimes) {
                            for(Map.Entry<Integer, List<Long>> entry : responseTimes.entrySet()) {
                                builder.append(entry.getKey());
                                builder.append("\n");
                                builder.append(entry.getValue().stream().map(Object::toString).collect(Collectors.joining(";")));
                                builder.append("\n");
                            }
                        }
                        Files.write(statPath, builder.toString().getBytes());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error with ", e);
                }
                LOGGER.log(Level.INFO, "Stat logger shutting down.");
            });

        } else {
            responseTimes = null;
            statThread = null;
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

        long start = 0;
        if(statPath != null) {
            start = System.currentTimeMillis();
        }
        String str = prepareRequest(stringList);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(str))
                .header("Accept", "application/json").header("Content-Type", "application/json").build();
        try{
            HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(send.statusCode() != 200) {
                LOGGER.log(Level.INFO, () -> "" + send.statusCode());
            }
            if(statPath != null){
                long resTime = System.currentTimeMillis() - start;
                responseTimes.compute(send.statusCode(),(k,v) ->
                {
                    if(v == null) {
                        v = new ArrayList<>();
                    }
                    v.add(resTime);
                    return v;
                });
            }
        } catch (IOException e){
            LOGGER.log(Level.INFO, e.getMessage());
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

    public void sendToServer(List<String> stringList, int millisDelay, boolean loop) {
        if(statPath != null) {
            statThread.start();
        }

        do {
            try {
                if (stringList.size() > batchSize) {
                    for (List<String> list : cutList(stringList, batchSize)) {
                        this.sendPost(sendURI, list);
                        Thread.sleep(millisDelay);
                    }
                } else {
                    this.sendPost(sendURI, stringList);
                    Thread.sleep(millisDelay);
                }
            } catch (IOException | InterruptedException e) {
                loop = false;
                Thread.currentThread().interrupt();
                LOGGER.log(Level.SEVERE, "Error", e);
            }
        } while(loop);
        if(statThread != null) {
            statThread.interrupt();
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
