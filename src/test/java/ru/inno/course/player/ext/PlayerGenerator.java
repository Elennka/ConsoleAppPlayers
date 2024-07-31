package ru.inno.course.player.ext;

import com.github.javafaker.Faker;
import ru.inno.course.player.model.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerGenerator {
    public static Set<Player> generate(int count) {
        Faker faker = new Faker();
        Set<Player> playerSet = new HashSet<>();
        for (int i = 0; i < count; i++) {
            String username=faker.name().username();
            int points = faker.number().numberBetween(1,1000);
            boolean isOnline=faker.bool().bool();
            playerSet.add(new Player(i + 1, "Nikita " + i + 1, i + 10, true));
        }
        return playerSet;
    }

}
