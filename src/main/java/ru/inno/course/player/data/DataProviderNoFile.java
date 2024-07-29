package ru.inno.course.player.data;

import ru.inno.course.player.model.Player;

import java.io.IOException;
import java.util.Collection;

public class DataProviderNoFile implements DataProvider{
    @Override
    public void save(Collection<Player> players) throws IOException {

    }

    @Override
    public Collection<Player> load() throws IOException {
        return null;
    }
}
