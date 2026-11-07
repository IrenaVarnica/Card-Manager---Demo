package com.example.demo;

import java.util.concurrent.ThreadLocalRandom;

public final class IdBroj {

    private IdBroj() {
    }

    public static int generiraj() {
        int osnova = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return osnova * 10 + kontrolnaZnamenka(osnova);
    }

    public static boolean jeValjan(int id) {
        if (id < 1000000 || id > 9999999) {
            return false;
        }
        return kontrolnaZnamenka(id / 10) == id % 10;
    }

    public static int kontrolnaZnamenka(int osnova) {
        int zbroj = 0;
        boolean dupliraj = true;

        for (int n = osnova; n > 0; n /= 10) {
            int znamenka = n % 10;
            if (dupliraj) {
                znamenka *= 2;
                if (znamenka > 9) {
                    znamenka -= 9;
                }
            }
            zbroj += znamenka;
            dupliraj = !dupliraj;
        }

        return (10 - zbroj % 10) % 10;
    }
}