package ru.ifmo.rain.khromova.student;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;//объект = бин.оп.над объектами
import java.util.function.Function;
import java.util.function.Predicate;//проверяет условие
import java.util.stream.Collectors;


public class StudentDB implements StudentQuery {
    private List<String> getFields(List<Student> students, Function<Student, String> s) {
        return students.stream().map(s).collect(Collectors.toList());
    }
    //stream().map() конвертирует students с пом. s
    //Collectors.toList() возвращает Collector, накапл.входн.эл.в список (из потока -> список)
    //возвр. список строк-полей students, обраб.s

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getFields(students, Student::getFirstName);
    }
    //возвр. массив объектов типа java.lang.reflect.Field, соответствующих всем открытым полям students

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getFields(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return getFields(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getFields(students, s -> s.getFirstName() + " " + s.getLastName());
    }


    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return (getFields(students, Student::getFirstName).stream().collect(Collectors.toCollection(TreeSet::new)));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream().min(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    private List<Student> sortStudentsByField(Collection<Student> students, Comparator<? super Student> comparator) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudentsByField(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStudentsByField(students, Comparator.comparing(Student::getLastName, String::compareTo).
                thenComparing(Student::getFirstName, String::compareTo).//лекс.комп. с функц., извлек.имя для сранения с compare
                thenComparingInt(Student::getId));//лексикогр.комп.
    }


    private List<Student> findStudentsByField(Collection<Student> students, Predicate<? super Student> predicate) {
        return sortStudentsByName(students).stream().filter(predicate).collect(Collectors.toList());
        //filter возвр.поток удовл.predicate
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudentsByField(students, s -> s.getFirstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudentsByField(students, s -> s.getLastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return findStudentsByField(students, s -> s.getGroup().equals(group));
    }


    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return students.stream().filter(s -> s.getGroup().equals(group))
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(Comparator.naturalOrder())));
    //ключ - фамилия имя- знач.
    }
}
