import collections.MyHashMap;
import collections.MyMap;
import model.CollidingKey;
import model.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainAppTest {
    @Test
    void putAndGetASingleElement() {
        MyMap<String, Person> myMapTest = new MyHashMap<>();
        Person testPerson = new Person("Michael", "michael@example.com", 30);

        myMapTest.put(testPerson.getEmail(), testPerson);

        assertEquals(1, myMapTest.size());
        assertEquals(testPerson, myMapTest.get(testPerson.getEmail()));
    }

    @Test
    void putAndGetMultipleElements() {
        MyMap<String, Person> myMapTest = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);
        Person testPerson2 = new Person("Lenny", "lenny@example.com", 62);
        Person testPerson3 = new Person("Harry", "harry@example.com", 35);

        myMapTest.put(testPerson1.getEmail(), testPerson1);
        myMapTest.put(testPerson2.getEmail(), testPerson2);
        myMapTest.put(testPerson3.getEmail(), testPerson3);

        assertEquals(3, myMapTest.size());
        assertEquals(testPerson1, myMapTest.get(testPerson1.getEmail()));
        assertEquals(testPerson2, myMapTest.get(testPerson2.getEmail()));
        assertEquals(testPerson3, myMapTest.get(testPerson3.getEmail()));
    }

    @Test
    void putAndGetElementWithNullKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Lenny", "lenny@example.com", 62);
        Person testPerson2 = new Person("Joe", "joe@example.com", 20);

        myMap.put(testPerson1.getEmail(), testPerson1);
        myMap.put(null, testPerson2);

        assertEquals(testPerson2, myMap.get(null));
        assertEquals(2, myMap.size());
    }

    @Test
    void putAndGetNullElement() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        String nullElementKey = "NullElementKey";

        myMap.put(nullElementKey, null);

        assertEquals(1, myMap.size());
        assertNull(myMap.get(nullElementKey));
    }

    @Test
    void putAndGetElementsWithEqualHash() {
        MyMap<CollidingKey, String> myMap = new MyHashMap<>();
        CollidingKey testValue1 = new CollidingKey("First");
        CollidingKey testValue2 = new CollidingKey("Second");
        CollidingKey testValue3 = new CollidingKey("Third");
        CollidingKey testValue4 = new CollidingKey("Fourth");
        CollidingKey testValue5 = new CollidingKey("Fifth");

        myMap.put(testValue1, testValue1.getName());
        myMap.put(testValue2, testValue2.getName());
        myMap.put(testValue3, testValue3.getName());
        myMap.put(testValue4, testValue4.getName());
        myMap.put(testValue5, testValue5.getName());


        assertEquals(5, myMap.size());
        assertEquals(testValue1.getName(), myMap.get(testValue1));
        assertEquals(testValue3.getName(), myMap.get(testValue3));
        assertEquals(testValue5.getName(), myMap.get(testValue5));
    }

    @Test
    void updateValueForAKey() {
        MyMap<String, Person> myMapTest = new MyHashMap<>();
        Person testCurrentPerson = new Person("Lenny", "lenny@example.com", 62);
        Person testUpdatePerson = new Person("Lenny Kravitz", "lenny@example.com", 62);

        myMapTest.put(testCurrentPerson.getEmail(), testCurrentPerson);
        myMapTest.put(testUpdatePerson.getEmail(), testUpdatePerson);

        assertEquals(1, myMapTest.size());
        assertEquals(testUpdatePerson, myMapTest.get(testUpdatePerson.getEmail()));
    }

    @Test
    void updateValueForNullKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Lenny", "lenny@example.com", 62);
        Person testPerson2 = new Person("Michael", "michael@example.com", 25);

        myMap.put(null, testPerson1);
        myMap.put(null, testPerson2);

        assertEquals(1, myMap.size());
        assertEquals(testPerson2, myMap.get(null));
    }

    @Test
    void getReturnsNullForMissingKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();

        assertNull(myMap.get("MissingKey"));
    }

    @Test
    void getSizeForEmptyMap() {
        MyMap<String, Person> myMap = new MyHashMap<>();

        assertEquals(0, myMap.size());
    }

    @Test
    void removeObjectForKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);

        myMap.put(testPerson1.getEmail(), testPerson1);

        assertEquals(1, myMap.size());
        assertEquals(testPerson1, myMap.remove(testPerson1.getEmail()));
        assertEquals(0, myMap.size());
    }

    @Test
    void removeObjectForMissingKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);

        myMap.put(testPerson1.getEmail(), testPerson1);

        assertEquals(1, myMap.size());
        assertNull(myMap.remove("MissingKey"));
        assertEquals(1, myMap.size());
    }

    @Test
    void removeElementForNullKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);

        myMap.put(null, testPerson1);

        assertEquals(1, myMap.size());
        assertEquals(testPerson1, myMap.remove(null));
        assertEquals(0, myMap.size());
    }
}
