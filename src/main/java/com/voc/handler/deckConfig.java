package com.voc.handler;

import java.util.Timer;
import java.util.TimerTask;

public class deckConfig {
    public static void timer(boolean isOn, int usrInp) {
        if (isOn) {
            Timer time = new Timer();
            TimerTask task = new TimerTask() {
                private int setTime = usrInp;

                @Override
                public void run() {
                    System.out.println(setTime);
                    setTime--;
                    if (setTime < 0) {
                        System.out.println("Flip the card");
                        time.cancel();
                    }
                }
            };
            time.scheduleAtFixedRate(task, 0, 1000);
        }
    }

    public static void cardPerSession(int howManyCard) {
        // im not sure this will have to cover all func?
        for (int i = 1; i < howManyCard; i++) {

        }
    }

}
