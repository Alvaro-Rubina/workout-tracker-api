package org.alvarub.workouttrackerproject.exception;

import java.io.IOException;

public class CloudinaryException extends RuntimeException {
    public CloudinaryException(String message, IOException e) {
        super(message);
    }
}
