package org.alvarub.workouttrackerproject.exception;

public class ExistingResourceException extends RuntimeException {
  public ExistingResourceException(String message) {
    super(message);
  }
}
