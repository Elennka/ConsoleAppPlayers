package ru.inno.course.player.ext;

import ru.inno.course.player.model.Player;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {

    public static void assertListValues(String[] listOfNickenames, Collection<Player> playerList) {

        assertEquals(listOfNickenames.length, playerList.size());
        for (Player player:playerList) {
            assertEquals(listOfNickenames[player.getId()-1],player.getNick());
            assertEquals(0, player.getPoints());
            assertTrue(player.isOnline());
        }

    }

}
