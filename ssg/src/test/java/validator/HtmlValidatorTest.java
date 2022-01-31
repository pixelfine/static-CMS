package validator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import parser.Builder;
import validator.HtmlValidator;

public class HtmlValidatorTest {

    @Test
    public void testValidateBuildOk() {
        String inputPath = "./test/";
        String outputPath = "./output_test/";

        try {
            for(int i = 1; i <= 4; i++) {
                Builder build = new Builder(inputPath, outputPath, i);
                build.buildRoot(true, false);

                for(int y = 0; y < 1000; y++) {
                    assertTrue(build.threadList.size() <= i && build.threadList.size() >= 0);
                    Thread.sleep(1);
                }
            }
        } catch (Exception e) {
        }

        assertTrue(HtmlValidator.validate(inputPath, outputPath), "HTML is not valid");
    }

    @Test
    public void testValidateOk() {
        String str = HtmlValidator.getContentFromFile("./test/validator/valid.html");
        assertTrue(HtmlValidator.validateHtmlContent(str), "HTML should be valid");
    }

    @Test
    public void testValidateKo() {
        String str = HtmlValidator.getContentFromFile("./test/validator/invalid.html");
        assertFalse(HtmlValidator.validateHtmlContent(str), "HTML should be invalid");
    }
}
