package com.springai.controller;

import org.apache.logging.log4j.util.PropertySource;

import java.util.*;
import java.util.stream.Collectors;

public class MyJava8Stream {
    public static void main(String[] args) {
        List<Emp> empList = prepareData();
        //Return distinct department names.
        empList.stream().map(Emp::getName).distinct().collect(Collectors.toList());
        //sort employee by salary
        empList.stream().sorted(Comparator.comparing(Emp::getSal)).collect(Collectors.toList());
        //sort employee by name abd find first
        empList.stream().sorted(Comparator.comparing(Emp::getName)).map(Emp::getName).findFirst().ifPresent(System.out::println);

        //Count employees department-wise.
        empList.stream().collect(Collectors.groupingBy(Emp::getDept, Collectors.counting()));

        //Maximum salary department-wise.
        empList.stream().collect(Collectors.groupingBy(Emp::getDept, Collectors.mapping(Emp::getSal, Collectors.maxBy(Comparator.naturalOrder()))));
        //Minimum salary department-wise.
        empList.stream().collect(Collectors.groupingBy(Emp::getDept, Collectors.mapping(Emp::getSal, Collectors.minBy(Comparator.naturalOrder()))));

        //Find all employee names in the IT department.

        empList.stream().filter(e-> "IT".equalsIgnoreCase(e.getDept()))
                .map(Emp::getName).collect(Collectors.toList());


        //Second highest salary.

        empList.stream().sorted(Comparator.comparing(Emp::getSal).reversed()).distinct().skip(1).findFirst().ifPresent(e -> System.out.println("second heighet salary"+e.getSal()));
                //soert
        //Top 3 highest-paid employees.
        empList.stream().sorted(Comparator.comparing(Emp::getSal).reversed()).limit(3).collect(Collectors.toList());

        //Return names of all employees whose salary is greater than 60,000, sorted alphabetically.
        empList.stream().filter(e-> e.getSal() > 60000).map(Emp::getName).sorted().forEach(System.out::println);
        //Return distinct department names sorted alphabetically.
        empList.stream().map(Emp::getDept).distinct().sorted().collect(Collectors.toList());
        //or
        empList.stream().map(Emp::getDept).sorted().collect(Collectors.toCollection(TreeSet::new));
        //Return the names of employees in the IT department, sorted by salary in descending order.
        empList.stream().filter(e-> "IT".equalsIgnoreCase(e.getDept()))
                .sorted(Comparator.comparing(Emp::getSal).reversed())
                .map(Emp::getName).collect(Collectors.toList());
        //Return the department-wise employee names.
        empList.stream().collect(Collectors.groupingBy(Emp::getDept, Collectors.mapping(Emp::getName
                , Collectors.toList())));

        //find the second heightest salary
        empList.stream().map(Emp::getSal).sorted(Comparator.reverseOrder()).skip(1).findFirst().ifPresent(System.out::println);

        //soert Nmes amphabeticalluy
        List<String> names = Arrays.asList("john", "alice", "bob", "david", "john");
        System.out.println("names in sprted order" + names.stream().sorted().collect(Collectors.toList()));
        //remove duplicates
        names.stream().distinct().collect(Collectors.toList());
    }

    private static List<Emp> prepareData() {
        List<Emp> employees = Arrays.asList(

                new Emp(101, "John",   "IT",       70000),
                new Emp(102, "Mike",   "HR",       45000),
                new Emp(103, "David",  "IT",       90000),
                new Emp(104, "Alex",   "Finance",  80000),
                new Emp(105, "Sam",    "HR",       50000),
                new Emp(106, "Bob",    "IT",       60000),
                new Emp(107, "Alice",  "Finance",  95000),
                new Emp(108, "Tom",    "Sales",    55000),
                new Emp(109, "Jerry",  "Sales",    55000),
                new Emp(110, "John",   "HR",       70000),
                new Emp(111, "Peter",  "IT",       40000),
                new Emp(112, "Mary",   "Finance",  75000),
                new Emp(113, "Steve",  "Sales",    65000),
                new Emp(114, "Chris",  "IT",       90000),
                new Emp(115, "Nancy",  "HR",       48000)

        );
        return employees;

    }
}

class Emp{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String name;
    private String dept;
    private int sal;

    public Emp(int id, String name, String dept, int sal) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.sal = sal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getSal() {
        return sal;
    }

    public void setSal(int sal) {
        this.sal = sal;
    }


}
