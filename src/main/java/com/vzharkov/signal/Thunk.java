package com.vzharkov.signal;

@FunctionalInterface
public interface Thunk {
    void apply();
}