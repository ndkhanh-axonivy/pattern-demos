package com.axonivy.demo.patterndemos.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.faces.bean.ApplicationScoped;

import com.axonivy.demo.patterndemos.Constants;


/**
 * Service for Date operations.
 *
 */
@ApplicationScoped
public class DateService {
	private static final DateService INSTANCE = new DateService();
	private static final DateTimeFormatter DEFAULT_INSTANT_FORMATTER = DateTimeFormatter.ofPattern(Constants.DEFAULT_INSTANT_PATTERN);

	/**
	 * Get service instance.
	 *
	 * @return Instance of the class
	 */
	public static DateService get() {
		return INSTANCE;
	}
	
	/**
	 * Format an {@link Instant} with default pattern.
	 *
	 * @param instant
	 * @return String
	 */
	public String toDefaultString(Instant instant) {
		return instant == null ? "" : LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DEFAULT_INSTANT_FORMATTER);
	}
	
	/**
	 * Format a duration as seconds with milliseconds.
	 *
	 * @param duration
	 * @return String
	 */
	public String toMilliString(Duration duration) {
		double millis = duration.getSeconds() + duration.getNano() / 1000000000.0;
		return duration == null ? "" : String.format("%.3f", millis);
	}
}
