package io.eshu.tradeassignment.exceptions;

import java.time.LocalDateTime;

public class CustomError {

	private String message;
	private LocalDateTime dateTime;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime date) {
		this.dateTime = date;
	}
	@Override
	public String toString() {
		return "CustomError [message=" + message + ", date=" + dateTime + "]";
	}
	
	
}
