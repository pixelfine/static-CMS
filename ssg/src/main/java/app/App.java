package app;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.net.URL;

import console.Option;
import console.CommandUser;
import parser.Analyzer;
import parser.Metadata;
import parser.Builder;
import server.Server;

public class App {

    public static void build(CommandUser command){
        // Single difference between md2html and buildsite is option --input-dir
        boolean hasInput = command.hasOption("--input-dir").isPresent();
        if (hasInput) buildsite(command);
        else md2html(command);
    }

    public static void serve(CommandUser command){
        Optional<Option> input   = command.hasOption("--input-dir");
        Optional<Option> output  = command.hasOption("--output-dir");
        Optional<Option> jobs    = command.hasOption("--jobs");
        Optional<Option> rebuild = command.hasOption("--rebuild-all");
        Optional<Option> port    = command.hasOption("--port");

        boolean buildAll = rebuild.isPresent();

        var in = input.isPresent() ? input.get().getArg().get()  : "./";
        var out = output.isPresent()  ? output.get().getArg().get() : "./_output";
        var cores = jobs.isPresent()  ? Integer.valueOf(jobs.get().getArg().get()) : Runtime.getRuntime().availableProcessors();
        var portNumber = port.isPresent() ? Integer.valueOf(port.get().getArg().get()) : 8080;

        new Server(in, out, cores, portNumber).launch();
    }

    public static void help(CommandUser command) throws Exception {
        try {
            InputStream is = App.class.getClassLoader().getResourceAsStream("docs/help.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void md2html(CommandUser command){
        Optional<Option> output = command.hasOption("--output-dir");

        var out = output.isPresent()  ? output.get().getArg().get() : "./_output/";

        command.getArgs().forEach(file -> {
            new Analyzer(new Metadata(), "", out, file).parse();
        });
    }

    private static void buildsite(CommandUser command){

        Optional<Option> input   = command.hasOption("--input-dir");
        Optional<Option> output  = command.hasOption("--output-dir");
        Optional<Option> jobs    = command.hasOption("--jobs");
        Optional<Option> rebuild = command.hasOption("--rebuild-all");
        Optional<Option> watch = command.hasOption("--watch");

        boolean buildAll = rebuild.isPresent() ? true : false;

        var in = input.isPresent() ? input.get().getArg().get()  : "./";
        var out = output.isPresent()  ? output.get().getArg().get() : in + "/_output/";
        var cores = jobs.isPresent()  ? Integer.valueOf(jobs.get().getArg().get()) : Runtime.getRuntime().availableProcessors();

        System.out.println("input :"+in);
		System.out.println("output:"+out);

        if(watch.isPresent()){
            while(true){
                new Builder(in, out, cores).buildRoot(buildAll,true);
                buildAll = false;
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        } else {
            new Builder(in, out, cores).buildRoot(buildAll,false);
        }
	}
}
