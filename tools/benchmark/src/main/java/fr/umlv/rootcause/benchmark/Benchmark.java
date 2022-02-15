package fr.umlv.rootcause.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Benchmark {
    public static void main(String[] args) throws ParseException, IOException {
        //args = new String[]{"-l", "10000000", "-F", "../logs/"};
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
            stream = getFilesFromFolder(Paths.get(line.getOptionValue("folder")),lineLimit(line));
        } else {
            stream = getStreamFromFile(Paths.get(line.getOptionValue("file")));
        }
        int lineCount = Integer.parseInt(line.getOptionValue("linesCount"));

        try {
            Path outputPath;
            if (line.hasOption("output")) {
                outputPath = Path.of(line.getOptionValue("output"));
            } else {
                outputPath = Path.of("out.txt");
            }
            System.out.println("Writing to file...");
            Files.write(outputPath,stream
                    .limit(lineLimit(line))
                    .collect(Collectors.toList()));
        } catch (NoSuchFileException e) {
            System.err.println("File not found, check file path : " + e.getMessage());
        }
    }

    private static Long lineLimit(CommandLine line){
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
                    synchronized (list){
                        list.add(x);
                    }
                });
                long l = atomicLong.get();
                if(l%250==0){
                    System.out.println(Math.round((float)l/limit*1000.0)/10.0 + " % complete");
                }
                if(l >= limit){
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
    private List<List<String>> cutList(List<String> originalList, int partitionSize) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i += partitionSize) {
            partitions.add(originalList.subList(i,
                    Math.min(i + partitionSize, originalList.size())));
        }
        return partitions;
    }
}