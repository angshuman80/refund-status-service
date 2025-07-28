package com.turbotax.refund.status.model;

public enum Status {
    PENDING,
    AVAILABLE,
    PROCESSED,
    DISPUTED,
    RECEIVED,
    NA;

    public boolean isAvailable() {
        return this == AVAILABLE || this == PROCESSED;
    }
}
