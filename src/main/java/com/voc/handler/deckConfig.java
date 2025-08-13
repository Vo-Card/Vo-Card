package com.voc.handler;

import java.util.Timer;
import java.util.TimerTask;

public class deckConfig {
    // public static void timer(boolean isOn, int usrInp) {
    // if (isOn) {
    // Timer time = new Timer();
    // TimerTask task = new TimerTask() {
    // private int setTime = usrInp;

    // @Override
    // public void run() {
    // System.out.println(setTime);
    // setTime--;
    // if (setTime < 0) {
    // System.out.println("Flip the card");
    // time.cancel();
    // }
    // }
    // };
    // time.scheduleAtFixedRate(task, 0, 1000);
    // }
    // }
    class timer implements Runnable {
        private int timer;

        timer() {
            this.timer = 5;
        }

        timer(int time) {
            this.timer = time;
        }

        @Override
        public void run() {
            for (int i = timer; i >= 0; i--) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Thread was interrupted");
                }
                if (i == 0) {
                    System.out.println("Time out");
                    continue;
                }
            }
        }
    }
}