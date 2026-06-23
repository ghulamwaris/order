package com.springai.controller;

public class MyEmployee {


    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    private String name;
    private double salary;

    //create constructor
    public MyEmployee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
    // create getter and setter for id
    public int getId() {
        return id;
    }
}
