package roomescape.store.entity;

import java.util.Objects;

public class Store {

    private final Long id;
    private final String name;

    private Store(String name) {
        this(null, name);
    }

    private Store(Long id, String name) {
        validate(name);
        this.id = id;
        this.name = name;
    }

    private void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("매장 이름은 필수입니다.");
        }
    }

    public static Store of(String name) {
        return new Store(name);
    }

    public static Store of(Long id, String name) {
        return new Store(id, name);
    }

    public static Store toEntity(Store store, Long id) {
        return new Store(id, store.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Store store = (Store) o;
        return Objects.equals(id, store.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
