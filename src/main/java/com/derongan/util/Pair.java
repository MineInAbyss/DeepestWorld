package com.derongan.util;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Pair<T> {
    public abstract T getFirst();

    public abstract T getSecond();

    public static <T> Pair<T> of(T newFirst, T newSecond) {
        return new AutoValue_Pair<>(newFirst, newSecond);
    }
}
