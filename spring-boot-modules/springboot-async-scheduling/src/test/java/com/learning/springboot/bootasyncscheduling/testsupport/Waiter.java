package com.learning.springboot.bootasyncscheduling.testsupport;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class Waiter {

    private Waiter() {
    }

    public static void await(String description, Duration timeout, Duration pollInterval, BooleanSupplier condition) {
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(timeout, "timeout");
        Objects.requireNonNull(pollInterval, "pollInterval");
        Objects.requireNonNull(condition, "condition");

        long deadlineNanos = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadlineNanos) {
            if (condition.getAsBoolean()) {
                return;
            }

            sleepQuietly(pollInterval);
        }

        throw new AssertionError("等待超时：" + description + "（timeout=" + timeout + ", pollInterval=" + pollInterval + "）");
    }

    private static void sleepQuietly(Duration pollInterval) {
        long millis = Math.max(1, pollInterval.toMillis());

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("等待被中断", e);
        }
    }
}

