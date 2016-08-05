package com.minemaarten.pokemodgo.client.gui.widget;

import java.util.List;

public interface IWidget{
    public void render();

    public void addTooltip(List<String> tooltip);
}
