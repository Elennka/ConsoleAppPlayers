package ru.inno.course.player;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.course.player.ext.Assertions;
import ru.inno.course.player.ext.MyTestWatcher;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;
import java.io.IOException;
import java.util.Collection;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MyTestWatcher.class)
public class PlayerServiceTestWithoutFile {
    private PlayerService service;
    private static final String NICKNAME = "Nikita";
    private static final String[] PLAYERSLIST = {"Nikita", "Petr", "Ivan"};
    private static final String FILE_WITH_INVALID_DATA = "./invaliddata.json";


    @BeforeEach
    public void setUp() throws IOException {
        service = new PlayerServiceImpl(false);
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Создаем игрока без файла и проверяем его значения по дефолту")
    public void iCanAddNewPlayerWithoutFile() {
        int nikitaId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(nikitaId);

        assertEquals(nikitaId, playerById.getId());
        assertEquals(0, playerById.getPoints());
        assertEquals(NICKNAME, playerById.getNick());
        assertTrue(playerById.isOnline());
    }

    @Test
    @Tag("Позитивный")
    @DisplayName("Проверить, что можно получить список без json файла и проверяем значения по дефолту")
    public void iCannotGetPlayerListWithoutJsonFile() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());

        service.createPlayersFromList(PLAYERSLIST);
        Assertions.assertListValues(PLAYERSLIST, service.getPlayers());
    }

    @Test
    @Tag("Негативный")
    @DisplayName("Проверка загрузки невалидного файла")
    public void iCannotUseInvalidFile() throws IOException {
        //
        ObjectMapper mapper = new ObjectMapper();
        assertThrows(JsonParseException.class, () -> mapper.readValue(FILE_WITH_INVALID_DATA, new TypeReference<Collection<Player>>() {
        }));
    }


}
