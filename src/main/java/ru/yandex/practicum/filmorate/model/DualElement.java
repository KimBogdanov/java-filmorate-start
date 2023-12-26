package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class DualElement<T, V> {
    T first;
    V second;

    public DualElement(T first, V second) {
        this.first = first;
        this.second = second;
    }
}
