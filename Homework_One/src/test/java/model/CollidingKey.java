package model;

import java.util.Objects;

public class CollidingKey {
    private final String name;

    public CollidingKey(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        CollidingKey collidingKey = (CollidingKey) o;
        return Objects.equals(collidingKey.name, name);
    }

    @Override
    public int hashCode() {
        return 42;
    }

    public String getName() {
        return name;
    }
}
