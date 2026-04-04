package org.shevchenko.taskmanagementspringapp.exception;

public class DropboxServiceException extends RuntimeException {
    public DropboxServiceException(String message) {
        super(message);
    }
}
