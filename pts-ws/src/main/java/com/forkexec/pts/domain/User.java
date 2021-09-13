package com.forkexec.pts.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final String id;
    private long tag;
    private AtomicInteger balance;

    public User(String id, AtomicInteger balance, long tag) {
        this.id = id;
        this.tag = tag;
        this.balance = balance;
    }

    public AtomicInteger getBalance() {
        return this.balance;
    }

    public long getTag() {
        return this.tag;
    }

    public String getId() {
        return this.id;
    }

    public void newBalance(int balance, long tag) {
        this.balance.set(balance);
        this.tag = tag;
    }
}
