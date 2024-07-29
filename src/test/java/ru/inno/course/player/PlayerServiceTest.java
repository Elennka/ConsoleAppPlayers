package ru.inno.course.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.inno.course.player.ext.MyTestWatcher;
import ru.inno.course.player.ext.PlayersAndPointsProvider;
import ru.inno.course.player.ext.PointsProvider;
import  ru.inno.course.player.ext.Assertions;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

// 1. Тест не должен настраивать свое окружение.
// 2. Главный код ничего не знает про тесты.
// 3. В тестах не должно быть if'ов

@ExtendWith(MyTestWatcher.class)
//@ExtendWith(BeforeEachDemo.class)

public class PlayerServiceTest {
    private PlayerService service;
    private static final String NICKNAME = "Nikita";
    private static final String[] PLAYERSLIST = {"Maxim", "Petr", "Ivan"};
    private static final int DEFAULT_POINTS = 5;


    // hooks - хуки
    @BeforeEach
    public void setUp() throws IOException {
        service = new PlayerServiceImpl(true);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Создаем список игроков и проверяем его значения")
    public void iCanGetListOfPlayesr() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());
        service.createPlayersFromList(PLAYERSLIST);
        Assertions.assertListValues(PLAYERSLIST, service.getPlayers());
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Создаем список игроков и проверяем его загрузку и значения из файла")
    public void iCanGetListOfPlayersFromFile() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());
        service.createPlayersFromList(PLAYERSLIST);
        Assertions.assertListValues(PLAYERSLIST, service.getPlayersFromFile());
    }

    @Test
    @Tags({@Tag("Позитивный"),@Tag("Critical")})
    @DisplayName("Создаем игрока и проверяем его значения по дефолту")
    public void iCanAddNewPlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());

        int nikitaId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(nikitaId);

        assertEquals(nikitaId, playerById.getId());
        assertEquals(0, playerById.getPoints());
        assertEquals(NICKNAME, playerById.getNick());
        assertTrue(playerById.isOnline());
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Нельзя создать дубликат игрока")
    public void iCannotCreateADuplicate() {
        service.createPlayer(NICKNAME);
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME));
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Нельзя получить несуществующего игрока")
    public void iCannotGetEmptyUser() {
        assertThrows(NoSuchElementException.class, () -> service.getPlayerById(9999));
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "MytooloNikita11"})
    @Tags({@Tag("Позитивный"),@Tag("Critical")})
    @DisplayName("Проверяем, что можем создать игрока с количеством символов от 1 до 15")
    public void iCanAddPlayerWithDifferentSizeOfNick(String names) {
        int playerId = service.createPlayer(names);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(names, playerById.getNick());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "MytooloNikitaNikita"})
    @Tag("Негативный")
    @DisplayName("Проверяем, что не можем создать игрока с пустым или длинным ником")
    public void iCannotAddPlayerWithEmptyAndLongName(String names) {
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(names));
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Удаляем существующего игрока и проверяем его отсутствие")
    public void iCanDeletePlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());
        int nikitaId = service.createPlayer(NICKNAME);
        service.deletePlayer(nikitaId);
        assertThrows(NoSuchElementException.class, () -> service.getPlayerById(nikitaId));
    }

    @Test
    @Tag("Негативный")
    @DisplayName("Нельзя удалить несуществующего игрока")
    public void iCannotDeleteUnExistUser() {
        assertThrows(NoSuchElementException.class, () -> service.deletePlayer(9999));
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Проверяем, что id всегда уникальный, что добавление игрока к списку происходит со следующим id")
    public void iCanAddPlayerWithNextId() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());
        service.createPlayersFromList(PLAYERSLIST);

        Collection<Player> listAfter = service.getPlayersFromFile();
        assertEquals(PLAYERSLIST.length, listAfter.size());

        service.deletePlayer(2);
        assertThrows(NoSuchElementException.class, () -> service.getPlayerById(2));
        int nikitaId = service.createPlayer(NICKNAME);
        assertEquals(PLAYERSLIST.length + 1, nikitaId);

    }


    @Test
    @Tag("Негативный")
    @DisplayName("Накинуть очки несуществующему игроку")
    public void iCannotAddPointsToUnexistUser() {
        service.createPlayer(NICKNAME);
        assertThrows(NoSuchElementException.class, () -> service.addPoints(9999, DEFAULT_POINTS));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, -50, 0, 100, -5000000})
    @Tag("Позитивный")
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints(int points) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, points);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(points, playerById.getPoints());
    }

    @ParameterizedTest
    @Tag("Позитивный")
    @ArgumentsSource(PointsProvider.class)
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints2(int pointsToAdd, int pointsToBe) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, pointsToAdd);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(pointsToBe, playerById.getPoints());
    }

    @ParameterizedTest
    @Tag("Позитивный")
    @ArgumentsSource(PlayersAndPointsProvider.class)
    @DisplayName("Добавление очков игроку c ненулевым балансом")
    public void iCanAddPoints3(Player player, int pointsToAdd, int pointsToBe) {
        int id = service.createPlayer(player.getNick());
        service.addPoints(id, player.getPoints());
        service.addPoints(id, pointsToAdd);
        Player playerById = service.getPlayerById(id);
        assertEquals(pointsToBe, playerById.getPoints());
    }

}

