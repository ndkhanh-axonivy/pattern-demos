package com.axonivy.demo.patterndemos.pojos;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;

//import com.axonivy.components.patterns.service.LogService;


/**
 * Represent a list of status messages and the most severe status that occurred.
 */
public class ServiceResult {
//	private static final Logger LOG = LogService.get().getLogger();
	public static final int MAX_TO_SHOW = 500;
	private int oks = 0;
	private int warnings = 0;
	private int errors = 0;
	private final List<Result> results = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public ServiceResult() {
	}

	/**
	 * Start with results.
	 *
	 * @param results
	 * @return
	 */
	public static ServiceResult of(Result...results) {
		var serviceResult = new ServiceResult();

		for (var result : results) {
			serviceResult.add(result);
		}

		return serviceResult;
	}

	/**
	 * Add a status without message.
	 *
	 * @param status
	 */
	public void add(ResultStatus status) {
		add(status, null);
	}

	/**
	 * Add a status and a message.
	 *
	 * If the logger was set, then log.
	 *
	 * @param status
	 * @param message
	 * @param arguments
	 */
	public void add(ResultStatus status, String message, Object...arguments) {
		add(status, message, (Throwable)null, arguments);
	}

	/**
	 * Add a status, a message and an exception.
	 *
	 * If no message is set, then the status will only be counted but not logged.
	 *
	 * If the logger was set, then log.
	 *
	 * @param status
	 * @param message if <code>null</code> count only and do not log
	 * @param th
	 * @param arguments
	 */
	public void add(ResultStatus status, String message, Throwable th, Object...arguments) {
		String text = "";
		if(message != null) {
			text = MessageFormat.format(message, arguments);
//			LOG.log(status.getLevel(), text, th);
			if(th != null) {
				text += System.lineSeparator() + ExceptionUtils.getStackTrace(th);
			}
		}

		add(new Result(status, text));
	}

	/**
	 * Add a result.
	 *
	 * @param result
	 */
	public void add(Result result) {
		results.add(result);
		switch(result.getStatus()) {
		case OK:
			oks++;
			break;
		case WARNING:
			warnings++;
			break;
		case ERROR:
			errors++;
			break;
		default:
			break;
		}
	}

	/**
	 * Merge in an other {@link ServiceResult}.
	 *
	 * @param result
	 */
	public void add(ServiceResult result) {
		results.addAll(result.results);
		oks += result.oks;
		warnings += result.warnings;
		errors += result.errors;
	}

	/**
	 * Return a String with basic memory information e.g. to show in long-running jobs.
	 *
	 * @return
	 */
	public String memoryFootprint() {
		Runtime runtime = Runtime.getRuntime();

		double mb = 1024 * 1024;

		long total = runtime.totalMemory();
		long used = total - runtime.freeMemory();

		System.gc();
		return String.format("total: %7.1f M used: %7.1f M", total / mb, used / mb);
	}

	/**
	 * Get the total status.
	 *
	 * The total status is the maximum of the individual status.
	 *
	 * @return
	 */
	public ResultStatus getStatus() {
		ResultStatus status = ResultStatus.OK;
		if(isError()) {
			status = ResultStatus.ERROR;
		}
		else if(isWarning()) {
			status = ResultStatus.WARNING;
		}

		return status;
	}

	/**
	 * Is the total status ok?
	 *
	 * @return
	 */
	public boolean isOk() {
		return !(isError() || isWarning());
	}

	/**
	 * Is the total status warning?
	 *
	 * @return
	 */
	public boolean isWarning() {
		return warnings > 0;
	}

	/**
	 * Is the total status error?
	 *
	 * @return
	 */
	public boolean isError() {
		return errors > 0;
	}

	/**
	 * Get the number of records which were ok.
	 *
	 * @return
	 */
	public int getOks() {
		return oks;
	}

	/**
	 * Get the number of collected warnings.
	 *
	 * @return
	 */
	public int getWarnings() {
		return warnings;
	}

	/**
	 * Get the number of collected errors.
	 *
	 * @return
	 */
	public int getErrors() {
		return errors;
	}

	/**
	 * Get the number of collected status.
	 *
	 * @return
	 */
	public int getTotal() {
		return oks + warnings + errors;
	}

	/**
	 * Get the collected results.
	 *
	 * @return
	 */
	public List<Result> getResults() {
		return results;
	}

	/**
	 * Get abbreviated results as text.
	 *
	 * @return
	 */
	public String toAbbreviatedStringList() {
		return toAbbreviatedStringList(MAX_TO_SHOW);
	}

	/**
	 * Get all results as text.
	 *
	 * @param maxResults
	 * @return
	 */
	public String toStringList() {
		return toAbbreviatedStringList(-1);
	}

	/**
	 * Get all results as text.
	 *
	 * @param maxResults
	 * @return
	 */
	public String toAbbreviatedStringList(int maxResults) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		out.format("Result: %s%n", getStatus());
		out.format("Total lines: %d OK: %d WARN: %d ERROR: %d%n".formatted(getTotal(), getOks(), getWarnings(), getErrors()));

		int toDrop = maxResults > 0 ? results.size() - maxResults : 0;
		int dropStart = 0;

		// does not make sense for only a few lines
		if(toDrop > 3) {
			dropStart = (results.size() - toDrop) / 2;
		}

		int count = 0;
		int dropped = 0;
		for (Result result : results) {
			if(dropStart > 0 && count >= dropStart && dropped < toDrop) {
				if(toDrop - dropped == 1) {
					out.println();
					out.format("... not showing %d entries.%n", toDrop);
					out.println();
				}
				dropped++;
			}
			else {
				out.format("%s: %s%n", result.getStatus(), result.getMessage());
			}
			count++;
		}
		return stringWriter.toString();
	}

	public static Result result(ResultStatus status, String message) {
		return new Result(status, message);
	}

	/**
	 * Represent a single result.
	 */
	public static class Result {
		private ResultStatus status;
		private String message;

		public Result() {
		}

		public Result(ResultStatus status, String message) {
			this.status = status;
			this.message = message;
		}

		public ResultStatus getStatus() {
			return status;
		}

		public void setStatus(ResultStatus status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	/**
	 * Status of {@link ServiceResult}.
	 */
	public static enum ResultStatus {
		OK(Level.INFO, FacesMessage.SEVERITY_INFO),
		WARNING(Level.WARN, FacesMessage.SEVERITY_WARN),
		ERROR(Level.ERROR, FacesMessage.SEVERITY_ERROR);

		private final Level level;
		private final Severity severity;

		private ResultStatus(Level level, Severity severity) {
			this.level = level;
			this.severity = severity;
		}

		/**
		 * Get the {@link Level} of the status.
		 *
		 * @return
		 */
		public Level getLevel() {
			return level;
		}

		/**
		 * Get the {@link Severity} of the status.
		 *
		 * @return
		 */
		public Severity getSeverity() {
			return severity;
		}
	}
}
