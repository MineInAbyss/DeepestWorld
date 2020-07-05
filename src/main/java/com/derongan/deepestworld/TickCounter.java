package com.derongan.deepestworld;

import java.util.concurrent.atomic.AtomicInteger;

public class TickCounter implements Runnable {
    public AtomicInteger tickCount = new AtomicInteger(0);

    @Override
    public void run() {
        tickCount.incrementAndGet();
    }
}
