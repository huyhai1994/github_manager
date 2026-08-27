package com.example.github_manager.repositories_sync.exception;

public class InvalidJobStateTransitionException extends RuntimeException {
    public InvalidJobStateTransitionException() {
        super("Invalid Job State Transition");
    }
}
