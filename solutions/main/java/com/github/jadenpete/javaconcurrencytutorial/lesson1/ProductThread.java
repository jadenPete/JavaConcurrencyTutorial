package com.github.jadenpete.javaconcurrencytutorial.lesson1;

public class ProductThread extends Thread {

    private final int start;
    private final int end;

    public int result = 1;

    public ProductThread(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        int currentSum = 1;

        for (int i = start; i < end; i++) {
            currentSum *= i;
        }

        result = currentSum;
    }
}
