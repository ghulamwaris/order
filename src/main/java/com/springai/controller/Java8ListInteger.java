package com.springai.controller;

import java.util.*;
import java.util.stream.Collectors;

public class Java8ListInteger {
    public static void main(String[] args) {
        List<Integer> myList = Arrays.asList(10,15,8,49,25,98,98,32,15);
        //find the duplicae
        Set<Integer> duplicates = new HashSet<>();

        Set<Integer> collect = myList.stream().filter(data -> !duplicates.add(data))
                .collect(Collectors.toSet());
        System.out.println(collect);
        myList.stream().max(Comparator.naturalOrder());

        List<String> detailsList = Arrays.asList("hi", "My name is java");
        Set<String> collect1 = detailsList.stream().flatMap(s -> Arrays.stream(s.split(" "))).collect(Collectors.toSet());
        System.out.println(collect1);

    }
}
