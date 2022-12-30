package com.github.druyaned.active_recorder.active;

public class ActivitiesSizeException extends RuntimeException {
    ActivitiesSizeException(final int MAX_SIZE) {
        super("max size (" + MAX_SIZE + ") of activities is reached");
    }
}
