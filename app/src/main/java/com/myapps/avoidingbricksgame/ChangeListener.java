package com.myapps.avoidingbricksgame;

import java.util.EventListener;

public interface ChangeListener extends EventListener {

    void stateChanged(LivesEvent e);

}

