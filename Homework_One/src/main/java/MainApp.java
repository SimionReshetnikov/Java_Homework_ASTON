import collections.MyHashMap;
import collections.MyMap;
import model.Person;

public class MainApp {
    public static void main(String[] args) {
        MyHashMap<String, Person> myMap = new MyHashMap<>();
        Person p1 = new Person("Ivan", "ivan@example.com", 32);
        Person p2 = new Person("Sveta", "sveta@example.com", 22);
        Person p3 = new Person("Dmitry", "dima@example.com", 28);
        Person p4 = new Person("Slava", "slava@example.com", 25);

        myMap.put(p1.getEmail(), p1);
        myMap.put(p2.getEmail(), p2);
        myMap.put(p3.getEmail(), p3);
        myMap.put(p4.getEmail(), p4);

        System.out.println(myMap.size());
    }
}
