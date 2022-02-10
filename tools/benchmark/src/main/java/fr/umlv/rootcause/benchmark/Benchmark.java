package fr.umlv.rootcause.benchmark;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Benchmark {
    public static void main(String[] args) throws ParseException, IOException {
        args = new String[]{"-l", "15", "-f", "log.txt"};

        final Options firstOptions = configFirstParameters();
        final Options options = configParameters(firstOptions);

        final CommandLineParser parser = new DefaultParser();
        final CommandLine firstLine = parser.parse(firstOptions, args, true);

        if (firstLine.hasOption("help")) {
            printHelp(options);
        }

        CommandLine line = null;
        try{
            line = parser.parse(options, args, true);
        }catch (MissingOptionException e){
            System.err.println(e.getMessage());
            printHelp(options);
        }

        Path path = Paths.get(line.getOptionValue("file"));
        int lineCount = Integer.parseInt(line.getOptionValue("linesCount"));

        try(Stream<String> lines = Files.lines(path).limit(lineCount))
        {
            if(line.hasOption("output")){
                System.out.println(line.getOptionValue("output"));
            }
            Files.write(path,(Iterable<String>)lines::iterator);
        }
        catch (NoSuchFileException e){
            System.err.println("File not found, check file path : " + e.getMessage());
        }
    }

    private static void printHelp(Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Benchmark", options, true);
        System.exit(0);
    }

    private static Options configFirstParameters() {

        final Option helpFileOption = Option.builder("h")
                .longOpt("help")
                .desc("Shows help message")
                .build();

        final Options firstOptions = new Options();

        firstOptions.addOption(helpFileOption);

        return firstOptions;
    }

    private static Options configParameters(final Options firstOptions) {

        final Option filePathOption = Option.builder("f")
                .longOpt("file") //
                .desc("File to read")
                .hasArg(true)
                .argName("filePath")
                .required(true)
                .build();

        final Option linesOption = Option.builder("l")
                .longOpt("linesCount")
                .desc("max number of lines to keep")
                .hasArg(true)
                .argName("lines")
                .required(true)
                .build();

        final Option outputOption = Option.builder("o")
                .longOpt("outputPath")
                .desc("path of Output File (automatically overwrites, be careful !), will be out.txt if left blank")
                .hasArg(true)
                .argName("output")
                .required(false)
                .build();

        final Options options = new Options();

        for (final Option fo : firstOptions.getOptions()) {
            options.addOption(fo);
        }
        options.addOption(filePathOption);
        options.addOption(linesOption);

        return options;
    }

}
