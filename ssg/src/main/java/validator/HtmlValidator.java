package validator;

import nu.validator.messages.MessageEmitter;
import nu.validator.messages.MessageEmitterAdapter;
import nu.validator.messages.TextMessageEmitter;
import nu.validator.servlet.imagereview.ImageCollector;
import nu.validator.source.SourceCode;
import nu.validator.validation.SimpleDocumentValidator;
import nu.validator.xml.SystemErrErrorHandler;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HtmlValidator {

    public static boolean validate(String inputPath, String outputPath){
        List<String> htmlFiles = getHtmlFilesNames(inputPath);

        int count = 0;

        for (String s : htmlFiles) {
            String content = getContentFromFile(outputPath + s);
            boolean okay = validateHtmlContent(content);
            if(!okay) System.out.println(s + " has not valid html");
            else count++;
        }
        return count == htmlFiles.size();
    }

    public static String getContentFromFile(String path) {
        BufferedReader reader = null;
        String content = "";

        try {
            reader =  new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        if (reader != null) {
            try {
                String line;

                while ((line = reader.readLine()) != null) {
                    content = content + line;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return content;
    }

    public static List<String> getHtmlFilesNames(String pathToFolder) {
        File f = new File (pathToFolder+"content/");
        File[] files = f.listFiles();
        List<String> htmlFiles = new ArrayList<>();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".md")) {
                    htmlFiles.add(files[i].getName().substring(0, files[i].getName().length()-3)+".html");
                }
            }
        }

        return htmlFiles;
    }

    public static boolean validateHtmlContent(String content) {
        final List<Exception> exceptions = new ArrayList<>();
        InputStream in;

        try {
            in = new ByteArrayInputStream(content.getBytes( "UTF-8"));
        } catch (Exception e) {
            return false;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SourceCode sourceCode = new SourceCode();
        ImageCollector imageCollector = new ImageCollector(sourceCode);
        MessageEmitter messageEmitter = new TextMessageEmitter(out, false);
        MessageEmitterAdapter adapter = new MessageEmitterAdapter(null, sourceCode, false, imageCollector, 0, false, messageEmitter);
        adapter.setErrorsOnly(true);

        Thread t = new Thread(null, null, "HtmlValidating", 10000000) {
            @Override
            public void run() {
                SimpleDocumentValidator validator = new SimpleDocumentValidator();
                try {
                    validator.setUpMainSchema("http://s.validator.nu/html5-rdfalite.rnc", new SystemErrErrorHandler());
                    validator.setUpValidatorAndParsers(adapter, true, false);
                    validator.checkHtmlInputSource(new InputSource(in));
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }

        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            exceptions.add(e);
        }

        return 0 == exceptions.size() && 0 == adapter.getErrors();
    }
}
