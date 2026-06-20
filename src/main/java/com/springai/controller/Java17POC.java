package com.springai.controller;

import javax.crypto.SealedObject;

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
        testSwitch("hello");
    }

    private static void testSwitch(String value) {
        switch (value) {
            case "hello" ->  System.out.println("Inside hello");
            case "world" -> System.out.println("Inside woorld");
            default -> System.out.println("No match");
        }
    }
}
