package com.springai.controller.interview;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExcetoerClass {

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<String> future = executor.submit(() -> {
            Thread.sleep(2000);
            return "Task Completed";
        });

        System.out.println(future.get());

        executor.shutdown();
    }

    public static void completableFutuer(){
        CompletableFuture<String> future =
                CompletableFuture.supplyAsync(() -> "Java")
                        .thenApply(s -> s + " 21")
                        .thenApply(String::toUpperCase);

        System.out.println(future.join());
    }

    /*
    ExecutorService executor = Executors.newFixedThreadPool(3);

CompletableFuture<String> user =
        CompletableFuture.supplyAsync(() -> "User", executor);

CompletableFuture<String> order =
        CompletableFuture.supplyAsync(() -> "Order", executor);

CompletableFuture<String> payment =
        CompletableFuture.supplyAsync(() -> "Payment", executor);

CompletableFuture.allOf(user, order, payment).join();

     */

    public static void completableFtutureWithExecutor(){
        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        CompletableFuture<String> future =
                CompletableFuture.supplyAsync(() -> {
                    return "Hello from Custom Executor";
                }, executor);

        System.out.println(future.join());

        executor.shutdown();
    }
}
