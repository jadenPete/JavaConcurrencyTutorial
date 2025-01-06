package com.github.jadenpete.javaconcurrencytutorial.lesson1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] arguments) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Thread count: ");

        int threadCount = scanner.nextInt();

        System.out.print("Start number: ");

        int start = scanner.nextInt();

        System.out.print("End number: ");

        int end = scanner.nextInt();

        List<ProductThread> threads = new ArrayList<>(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int numbersPerThread = (int) Math.ceil((float) (end - start) / threadCount);
            ProductThread thread = new ProductThread(
                    start + numbersPerThread * i,
                    start + numbersPerThread * (i + 1)
            );

            threads.add(thread);
            thread.start();
        }

        int result = 1;

        for (ProductThread thread : threads) {
            thread.join();

            result *= thread.result;
        }

        System.out.printf("Result: %d\n", result);
    }
}
