package ru.inno.course.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.course.player.ext.PlayerGenerator;
import ru.inno.course.player.ext.PlayerServerResolver;
import ru.inno.course.player.ext.Players;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PlayerServerResolver.class)
public class PlayerServiceStorageTest {

    private static final String NICKNAME = "Nikita";


    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }

    @Test
    @DisplayName("Проверить запуск с пустым хранилищем")
    public void iCanAddNewPlayer(@Players(0) PlayerService service) {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());

    }

    @Test
    @DisplayName("Проверить запуск с 1000 пользователями")
    public void loadTest(@Players(1000) PlayerService service) {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(1000, listBefore.size());

    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Проверяем, что id всегда уникальный, что добавление игрока к списку происходит со следующим id")
    public void iCanAddPlayerWithNextId(@Players(5) PlayerService service) {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(5, listBefore.size());
        int numberOfPlayers = listBefore.size();
        service.deletePlayer(2);
        assertThrows(NoSuchElementException.class, () -> service.getPlayerById(2));
        int nikitaId = service.createPlayer(NICKNAME);
        assertEquals(numberOfPlayers + 1, nikitaId);

    }


    @Test
    @Tag("Негативный")
    @DisplayName("Проверка загрузки невалидного файла, что данные не загрузились")
    public void iCannotUseInvalidFile() throws IOException {
        Files.writeString(Path.of("./data.json"), "Никнейм:Петя");
        PlayerService service=new PlayerServiceImpl();
        Collection<Player> playerList = service.getPlayers();
        assertEquals(0, playerList.size());

    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Проверить, что можно получить список без json файла ")
    public void iCannotGetPlayerListWithoutJsonFile() {
        //создаем список
        PlayerService service=new PlayerServiceImpl();
        for (int i=1; i<=5; i++){
            service.createPlayer("Никита"+i);
        }
        Collection<Player> playerList = service.getPlayers();
        assertEquals(5, playerList.size());
    }
}
