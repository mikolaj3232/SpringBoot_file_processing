package com.rarToZip.RarToZip.myDictinary;

public class DictionaryItem<T,V>{
    private T key;
    private V value;

    public DictionaryItem(T key, V value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
