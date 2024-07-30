package ru.inno.course.player.ext;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class MyTestWatcher implements TestWatcher, BeforeAllCallback, AfterAllCallback {
    private Map<String, Integer> statuses;
    public static final String SUCCESS = "s";
    public static final String FAIL = "f";
    public static final String ABORT = "a";
    public static final String DISABLE = "d";


    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        Integer current = statuses.get(DISABLE);
        current += 1;
        statuses.put(DISABLE, current);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {

        Integer current = statuses.get(SUCCESS);
        current += 1;
        statuses.put(SUCCESS, current);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        Integer current = statuses.get(ABORT);
        current += 1;
        statuses.put(ABORT, current);

    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
//        TestWatcher.super.testFailed(context, cause);
//        if (context.getTestMethod().get().isAnnotationPresent(Tags.class)) {
//            Tag[] tags = context.getTestMethod().get().getAnnotation(Tags.class).value();
//            for (Tag tag : tags) {
//                if (tag.value().equalsIgnoreCase("CRITICAL")) {
//                    System.out.println("Упал критичный тест");
//                }
//            }
//        }
        Integer current = statuses.get(FAIL);
        current += 1;
        statuses.put(FAIL, current);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

        String head = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Title</title>
                </head>
                <body>
                <table>
                """;
        String tail = """
                </table>
                </body>
                </html>
                """;

        String content="";

        Integer num=statuses.get(SUCCESS);
        content+="<str><td>Succes</td><td>"+num+"</td></tr>";

        num=statuses.get(FAIL);
        content+="<str><td>Fail</td><td>"+num+"</td></tr>";

        num=statuses.get(ABORT);
        content+="<str><td>Abort</td><td>"+num+"</td></tr>";

        num=statuses.get(DISABLE);
        content+="<str><td>DISABLE</td><td>"+num+"</td></tr>";

        String report = head + content + tail;
        Files.writeString(Path.of("report.html"),report);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        statuses = new HashMap<>();
        {
            statuses.put(SUCCESS, 0);
            statuses.put(FAIL, 0);
            statuses.put(ABORT, 0);
            statuses.put(DISABLE, 0);
        }

    }
}
