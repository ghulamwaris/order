package com.springai.controller;

import javax.crypto.SealedObject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Java17POC {

    public static void main(String[] args) {
        var name = "hello";
        System.out.println(name);
        Object test = "hello";
        if (test instanceof String str && str.equals(name)) {
            System.out.println("both are equal");
        }

        var textblock = """
                this is ghulam waris 
                and I am learning java 17""";
        System.out.println(textblock);
        //testSwitch("hello");
        JAVA8API();
    }

    private static void testSwitch(String value) {
        switch (value) {
            case "hello" ->  System.out.println("Inside hello");
            case "world" -> System.out.println("Inside woorld");
            default -> System.out.println("No match");
        }
    }

    public static void JAVA8API() {
        var list = java.util.List.of("hello", "world", "java","hello");

        var myStrignwithSpace = "hello world";
        //java 8 code to list duplicate elements in the list
        Set<String> duplicte= new HashSet<>();
                list.stream().filter(s -> !duplicte.add(s)).collect(Collectors.toSet()).forEach(System.out::println);
        //Each character Frequency count in java 8 need to be implemented
        String str = "helloworld";

        str.chars().mapToObj(c -> (char) c)
                        .collect(Collectors.groupingBy(c -> c, Collectors.counting())).forEach((k, v) -> System.out.println(k + " : " + v));

        Arrays.stream(myStrignwithSpace.split(" ")).collect(Collectors.groupingBy(s -> s, Collectors.counting())).forEach((k, v) -> System.out.println(k + " : " + v));

//        str.chars().mapToObj(c -> (char) c)
//                .collect(Collectors.groupingBy(c -> c, Collectors.counting())).
//                forEach((k, v) -> System.out.println(k + " : " + v));
        //collecos.groupingBy(c -> c, collector.counting())
    }

    public static void playWithJava8AndMyEmployee(){
        var employeeList = java.util.List.of(new MyEmployee(1, "Ghulam Waris", 1000),
                new MyEmployee(2, "John Doe", 2000),
                new MyEmployee(3, "Jane Doe", 3000));
        employeeList.stream().sorted(Comparator.comparing(MyEmployee :: getSalary).reversed()).skip(1).findFirst().ifPresent(e -> System.out.println(e.getName()));
        //get the name of employee with id 2
        String name = employeeList.stream().filter(e -> e.getId() == 2).findFirst().map(MyEmployee::getName).orElse("Not found");
        System.out.println(name);
    }
}
