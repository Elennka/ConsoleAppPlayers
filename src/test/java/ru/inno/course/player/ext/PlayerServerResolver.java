package ru.inno.course.player.ext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.helpers.AnnotationHelper;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class PlayerServerResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return true;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        Players annotation = AnnotationHelper.findAnnotation(parameterContext.getAnnotatedElement(),Players.class);
        int num = annotation.value();

        Set<Player> generated=PlayerGenerator.generate(num);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Path.of("./data.json").toFile(),generated);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PlayerServiceImpl();
    }
}
