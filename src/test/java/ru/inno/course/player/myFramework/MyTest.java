package ru.inno.course.player.myFramework;

import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MyTest {

    public static void main(String[] args) throws IOException {
        String nickname = "Nikita";
        Files.deleteIfExists(Path.of("./data.json"));
        PlayerService service = new PlayerServiceImpl(true);

        TestCase case1 = new TestCase("1. добавить игрока - проверить наличие в списке", () -> {
            Collection<Player> listBefore = service.getPlayers();
            if (listBefore.size() != 0) {
                throw new Exception("При пустом файле получили непустой список");
            }
            int nikitaId = service.createPlayer(nickname);
            Player playerById = service.getPlayerById(nikitaId);
            boolean result = playerById.getId() == nikitaId
                    && playerById.getPoints() == 0
                    && playerById.isOnline()
                    && playerById.getNick().equals(nickname);
        });

        TestCase case2 = new TestCase("2. создать дубликат (имя уже занято)", () -> {
            try {
                int duplicateId = service.createPlayer(nickname);
                throw new Exception("Тест провален! Создали дубликат: " + duplicateId);
            } catch (IllegalArgumentException iae) {
            }
        });

        TestCase case3 = new TestCase("3. получить игрока по id", () -> {
            try {
                service.getPlayerById(9999);
                throw new Exception("Тест провален! Получили игрока по id : " + 9999);
            } catch (NoSuchElementException nsee) {
            }
        });

        TestCase case4 = new TestCase("4. получить игрока по id", () -> {
            System.out.println("Hello world");
        });

        Map<TestCase, Boolean> testCases = new HashMap<>();
        testCases.put(case1, false);
        testCases.put(case2, false);
        testCases.put(case3, false);

        testCases.keySet().forEach((t) -> {
            boolean r = test(t);
            testCases.put(t, r);
        });

        System.out.println("Всего тестов: " + testCases.size());
        System.out.println("Упавших тестов: " + testCases.size());
        System.out.println("Успешных тестов: " + testCases.size());


    }

    public static boolean test(TestCase testCase) {
        System.out.printf("Выполняем тест %s \n", testCase.name());
        try {
            testCase.testFunction().runTest();
            return true;
        } catch (Exception e) {
            System.out.println("Тест провален, поймали странное исключение");
            return false;
        }
    }
}
