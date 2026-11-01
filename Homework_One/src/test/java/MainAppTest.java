import collections.MyHashMap;
import collections.MyMap;
import model.CollidingKey;
import model.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainAppTest {
    @Test
    void putAndGetSingleElement() {
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
    void putAndGetElementWithNullValue() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        String nullElementKey = "NullElementKey";

        myMap.put(nullElementKey, null);

        assertEquals(1, myMap.size());
        assertNull(myMap.get(nullElementKey));
    }

    @Test
    void putAndGetElementsWithCollidingHash() {
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
        for (CollidingKey key : List.of(testValue1, testValue2, testValue3, testValue4, testValue5)) {
            assertEquals(key.getName(), myMap.get(key));
        }
    }

    @Test
    void updateValueForExistingKey() {
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
    void updateValueInCollisionChain() {
        MyMap<CollidingKey, String> myMap = new MyHashMap<>();
        CollidingKey testKey1 = new CollidingKey("First");
        CollidingKey testKey2 = new CollidingKey("Second");
        String value1 = "old-first-value";
        String value2 = "old-second-value";
        String newValue2 = "new-second-value";

        myMap.put(testKey1, value1);
        myMap.put(testKey2, value2);
        myMap.put(testKey2, newValue2);

        assertEquals(2, myMap.size());
        assertEquals(newValue2, myMap.get(testKey2));
        assertEquals(value1, myMap.get(testKey1));
    }

    @Test
    void getReturnsNullForMissingKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();

        assertNull(myMap.get("MissingKey"));
    }

    @Test
    void newMapHasZeroSize() {
        MyMap<String, Person> myMap = new MyHashMap<>();

        assertEquals(0, myMap.size());
    }

    @Test
    void removeExistingKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);

        myMap.put(testPerson1.getEmail(), testPerson1);

        assertEquals(1, myMap.size());
        assertEquals(testPerson1, myMap.remove(testPerson1.getEmail()));
        assertEquals(0, myMap.size());
    }

    @Test
    void removeMissingKeyReturnsNullAndDoesNotChangeSize() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);

        myMap.put(testPerson1.getEmail(), testPerson1);

        assertEquals(1, myMap.size());
        assertNull(myMap.remove("MissingKey"));
        assertEquals(1, myMap.size());
    }

    @Test
    void removeElementByNullKey() {
        MyMap<String, Person> myMap = new MyHashMap<>();
        Person testPerson1 = new Person("Michael", "michael@example.com", 30);

        myMap.put(null, testPerson1);

        assertEquals(1, myMap.size());
        assertEquals(testPerson1, myMap.remove(null));
        assertEquals(0, myMap.size());
    }

    @Test
    void removeElementFromMiddleChain() {
        MyMap<CollidingKey, String> myMap = new MyHashMap<>();
        CollidingKey testValue1 = new CollidingKey("First");
        CollidingKey testValue2 = new CollidingKey("Second");
        CollidingKey testValue3 = new CollidingKey("Third");

        myMap.put(testValue1, testValue1.getName());
        myMap.put(testValue2, testValue2.getName());
        myMap.put(testValue3, testValue3.getName());

        assertEquals(3, myMap.size());

        String removedValue = myMap.remove(testValue2);

        assertEquals(testValue2.getName(), removedValue);
        assertNull(myMap.get(testValue2));
        assertEquals(2, myMap.size());
        assertEquals(testValue1.getName(), myMap.get(testValue1));
        assertEquals(testValue3.getName(), myMap.get(testValue3));
    }

    @Test
    void removeElementFromBeginningChain() {
        MyMap<CollidingKey, String> myMap = new MyHashMap<>();
        CollidingKey testValue1 = new CollidingKey("First");
        CollidingKey testValue2 = new CollidingKey("Second");
        CollidingKey testValue3 = new CollidingKey("Third");

        myMap.put(testValue1, testValue1.getName());
        myMap.put(testValue2, testValue2.getName());
        myMap.put(testValue3, testValue3.getName());

        assertEquals(3, myMap.size());

        String removedValue = myMap.remove(testValue1);

        assertEquals(testValue1.getName(), removedValue);
        assertNull(myMap.get(testValue1));
        assertEquals(2, myMap.size());
        assertEquals(testValue2.getName(), myMap.get(testValue2));
        assertEquals(testValue3.getName(), myMap.get(testValue3));
    }

    @Test
    void removeElementFromEndChain() {
        MyMap<CollidingKey, String> myMap = new MyHashMap<>();
        CollidingKey testValue1 = new CollidingKey("First");
        CollidingKey testValue2 = new CollidingKey("Second");
        CollidingKey testValue3 = new CollidingKey("Third");

        myMap.put(testValue1, testValue1.getName());
        myMap.put(testValue2, testValue2.getName());
        myMap.put(testValue3, testValue3.getName());

        assertEquals(3, myMap.size());

        String removedValue = myMap.remove(testValue3);

        assertEquals(testValue3.getName(), removedValue);
        assertNull(myMap.get(testValue3));
        assertEquals(2, myMap.size());
        assertEquals(testValue1.getName(), myMap.get(testValue1));
        assertEquals(testValue2.getName(), myMap.get(testValue2));
    }

    @Test
    void resizeTriggeredWhenThresholdIsExceeded() throws Exception {
        MyMap<String, Person> myMap = new MyHashMap<>();
        List<Person> personList = List.of(
                new Person("Joe", "joe@example.com", 20),
                new Person("Lenny", "lenny@example.com", 25),
                new Person("Bob", "bob@example.com", 22),
                new Person("Gaga", "gag@example.com", 18),
                new Person("Sam", "sam@example.com", 34),
                new Person("Tom", "tom@example.com", 40),
                new Person("Zoy", "zoy@example.com", 37),
                new Person("Michael", "muchael@example.com", 28),
                new Person("Tiler", "tiler@example.com", 25),
                new Person("Bread", "bread@example.com", 18),
                new Person("Anna", "anna@example.com", 30),
                new Person("Steve", "steve@example.com", 34),
                new Person("Jim", "jim@example.com", 31)
        );

        for (Person person : personList) {
            myMap.put(person.getEmail(), person);
        }

        Field field = MyHashMap.class.getDeclaredField("table");
        field.setAccessible(true);
        assertEquals(32, ((Object[]) field.get(myMap)).length);
        for (Person person : personList) {
            assertEquals(person, myMap.get(person.getEmail()));
        }
    }

    @Test
    void resizeNotTriggeredAtThresholdBoundary() throws Exception {
        MyMap<String, Person> myMap = new MyHashMap<>();
        List<Person> personList = List.of(
                new Person("Joe", "joe@example.com", 20),
                new Person("Lenny", "lenny@example.com", 25),
                new Person("Bob", "bob@example.com", 22),
                new Person("Gaga", "gag@example.com", 18),
                new Person("Sam", "sam@example.com", 34),
                new Person("Tom", "tom@example.com", 40),
                new Person("Zoy", "zoy@example.com", 37),
                new Person("Michael", "muchael@example.com", 28),
                new Person("Tiler", "tiler@example.com", 25),
                new Person("Bread", "bread@example.com", 18),
                new Person("Anna", "anna@example.com", 30),
                new Person("Steve", "steve@example.com", 34)
        );

        for (Person person : personList) {
            myMap.put(person.getEmail(), person);
        }

        Field field = MyHashMap.class.getDeclaredField("table");
        field.setAccessible(true);
        assertEquals(16, ((Object[]) field.get(myMap)).length);
    }
}
