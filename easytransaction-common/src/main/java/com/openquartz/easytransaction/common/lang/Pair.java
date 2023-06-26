package com.openquartz.easytransaction.common.lang;

public class Pair<K,V> {

    private K k;

    private V v;

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    public Pair() {
    }

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public static<K,V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }
}
