package com.myapps.avoidingbricksgame;



import java.util.ArrayList;


public class Lives {
    private int numLives;
    private ArrayList<ChangeListener> listenerList = new ArrayList<>();

    public int getNumLives() {
        return numLives;
    }

    public  Lives(){
        numLives = 3;
    }

    public void setNumLives(int n){
        if (n >= 0){
            this.numLives = n;
            notify(new LivesEvent(n));
        }
    }
    public void subscribe(ChangeListener listener){
        listenerList.add(listener);
    }

    public  void unsubscribe(ChangeListener listener){
        listenerList.remove(listener);
    }

    public void notify(LivesEvent e){
        for (ChangeListener listener: listenerList) {
            listener.stateChanged(e);
        }
    }
}

class LivesEvent {

    private int numLivesChange;

    public LivesEvent(int num){
        numLivesChange = num;
    }

    public int getNumLivesChange() {
        return numLivesChange;
    }

}