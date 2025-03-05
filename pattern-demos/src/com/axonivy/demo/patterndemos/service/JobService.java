package com.axonivy.demo.patterndemos.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.message.MessageFormatMessage;

import com.axonivy.demo.patterndemos.Constants;
import com.axonivy.demo.patterndemos.dao.JobStatusDAO;
import com.axonivy.demo.patterndemos.entities.JobStatus;
import com.axonivy.demo.patterndemos.entities.Lock;
import com.axonivy.demo.patterndemos.enums.JobRunStatus;
import com.axonivy.demo.patterndemos.pojos.ServiceResult;
import com.axonivy.demo.patterndemos.pojos.ServiceResult.ResultStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ch.ivyteam.ivy.bpm.error.BpmError;
import ch.ivyteam.ivy.environment.Ivy;

public class JobService {
	private static final JobService INSTANCE = new JobService();
	public static String OK_MESSAGE = "OK";
	private static final Map<String, JobDescription> jobRepository = Collections.synchronizedMap(new HashMap<>());
	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

	private JobService() {
	}

	public static JobService get() {
		return INSTANCE;
	}

	/**
	 * Run a Job.
	 *
	 * @param additionalJobData
	 * @param jobDescription
	 *
	 * @return
	 */
	public ServiceResult runJob(String jobName, String additionalJobData) {
		JobDescription jobDescription = findJobDescription(jobName);
		if(jobDescription == null) {
			BpmError
			.create(Constants.ERROR_BASE + "job:not:found")
			.withMessage("Could not find job ''" + jobName + "'' in job repository.")
			.throwError();
		}
		return runJob(jobDescription, additionalJobData);
	}

	/**
	 * Run a Job.
	 *
	 * @param jobDescription
	 * @param additionalJobData
	 * @return
	 */
	public ServiceResult runJob(JobDescription jobDescription, String additionalJobData) {
		return runJob(jobDescription.getJobName(), true, null, jobDescription.getJobFunction(), additionalJobData);
	}

	/**
	 * Run a Job.
	 *
	 * @param jobName name of job used to save job status
	 * @param locked if <code>true</code>, than only one instance of the job can run at a time
	 * @param lockTimeout timeout for the lock, if <code>null</code>, then no timeout
	 * @param function function to execute
	 * @param additionalJobData
	 * @return
	 */
	public ServiceResult runJob(String jobName, boolean locked, Duration lockTimeout, Function<JobStatus, ServiceResult> function, String additionalJobData) {
		Instant startTime = Instant.now();
		try {
			
			ServiceResult result = null;
			try {
				if(locked) {
					result = LockService.get().doLocked(lockName(jobName), lockTimeout, () -> runInternal(jobName, function, additionalJobData));
				}
				else {
					result = runInternal(jobName, function, additionalJobData);
				}
			} catch (Throwable e) {
				result = new ServiceResult();
				result.add(ResultStatus.ERROR, "Exception running job ''{0}''.", e, jobName);
			}
			return result;
		}
		finally {
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			Ivy.log().info(new MessageFormatMessage("Ending Job ''{0}'', running for a duration of {1}s.", jobName, DateService.get().toMilliString(duration)).getFormattedMessage());
		}
	}

	/**
	 * Get the lock of this job.
	 *
	 * @param jobName
	 * @return
	 */
	public Lock getJobLock(String jobName) {
		return LockService.get().getLock(lockName(jobName));
	}

	/**
	 * Is the job locked?
	 *
	 * @param jobName
	 * @return
	 */
	public boolean isLocked(String jobName) {
		return LockService.get().isLocked(lockName(jobName));
	}

	/**
	 * Force unlock of job.
	 *
	 * @param jobName
	 * @param tryUntil
	 * @return
	 */
	public boolean forceUnlock(String jobName, Duration tryUntil) {
		return LockService.get().forceUnlock(lockName(jobName), tryUntil);
	}

	/**
	 * Get the current job status or create one with at least the name set.
	 *
	 * @param jobName
	 * @return
	 */
	public JobStatus loadJobStatus(String jobName) {
		var jobStatus = JobStatusDAO.get().findByName(jobName);
		if(jobStatus == null) {
			jobStatus = new JobStatus();
			jobStatus.setName(jobName);
		}
		return jobStatus;
	}

	/**
	 * Load current version of {@link JobStatus}.
	 *
	 * @param status
	 * @return
	 */
	public JobStatus loadCurrentJobStatus(JobStatus status) {
		return loadJobStatus(status.getName());
	}

	/**
	 * Save the current job status.
	 *
	 * @param jobStatus
	 * @return
	 */
	public JobStatus saveJobStatus(JobStatus jobStatus) {
		var current = loadJobStatus(jobStatus.getName());
		current.setRunStatus(jobStatus.getRunStatus());
		current.setStartTime(jobStatus.getStartTime());
		current.setEndTime(jobStatus.getEndTime());
		current.setLastSuccessStartTime(jobStatus.getLastSuccessStartTime());
		current.setLastSuccessEndTime(jobStatus.getLastSuccessEndTime());
		current.setJobData(jobStatus.getJobData());
		current.setAdditionalJobData(StringUtils.isBlank(jobStatus.getAdditionalJobData()) ? null : jobStatus.getAdditionalJobData());
		current.setMessage(jobStatus.getMessage());
		return JobStatusDAO.get().save(current);
	}

	/**
	 * Get the name of the lock of this job.
	 *
	 * @param jobName
	 * @return
	 */
	protected String lockName(String jobName) {
		return "job:" + jobName;
	}

	/**
	 * Start a job with it's previous {@link JobStatus} and log {@link JobStatus} and errors to DB.
	 *
	 * @param <R>
	 * @param jobName
	 * @param function
	 * @param additionalJobData
	 * @return
	 */
	protected <R> R runInternal(String jobName, Function<JobStatus, R> function, String additionalJobData) {
		R result = null;

		// Find last job status in database.
		JobStatus lastJobStatus = logJobStart(jobName, additionalJobData);

		boolean ok = false;
		String message = null;
		try {

			// Call job with data of previous run. If the Job wants to access data of the current run, it can load it from the database with loadJobStatus.
			result = function.apply(lastJobStatus);

			ok = true;
			message = OK_MESSAGE;

			if(result instanceof ServiceResult) {
				ServiceResult serviceResult = (ServiceResult) result;
				if(serviceResult.isError()) {
					ok = false;
				}
				message = serviceResult.toAbbreviatedStringList();
			}
		}
		catch(Throwable t) {
			message = ExceptionUtils.getStackTrace(t);
			throw t;
		}
		finally {
			logJobEnd(jobName, ok, message);
		}

		return result;
	}

	/**
	 * Log start of job.
	 *
	 * @param jobName
	 * @param additionalJobData
	 * @return last JobStatus
	 */
	protected JobStatus logJobStart(String jobName, String additionalJobData) {
		JobStatus jobStatus = loadJobStatus(jobName);
		JobStatus lastJobStatus = loadJobStatus(jobName);

		jobStatus.setAdditionalJobData(StringUtils.isBlank(additionalJobData) ? null : additionalJobData);
		jobStatus.setRunStatus(JobRunStatus.RUNNING);
		jobStatus.setStartTime(Instant.now());
		jobStatus.setEndTime(null);
		jobStatus.setMessage(null);
		jobStatus.setJobData(null);

		jobStatus = saveJobStatus(jobStatus);
		
		Ivy.log().info(new MessageFormatMessage("Job ''{0}'' started at ''{1}'', additionalJobData: ''{2}''", jobStatus.getName(), DateService.get().toDefaultString(jobStatus.getStartTime()), jobStatus.getAdditionalJobData()).getFormattedMessage());
		return lastJobStatus;
	}

	/**
	 * Log end and status of job.
	 *
	 * @param jobName
	 * @param ok
	 * @param message
	 * @return
	 */
	protected JobStatus logJobEnd(String jobName, boolean ok, String message) {
		JobStatus jobStatus = loadJobStatus(jobName);

		if(jobStatus.getRunStatus() != JobRunStatus.RUNNING) {
			BpmError.create(Constants.ERROR_BASE + "wrongjobstatus")
			.withMessage("Cannot end job '" + jobName + "' because it is not marked as running.")
			.throwError();
		}

		jobStatus.setRunStatus(ok ? JobRunStatus.OK : JobRunStatus.FAILED);
		jobStatus.setMessage(StringUtils.abbreviateMiddle(message, "...<truncated>...", JobStatus.MAX_MESSAGE_LENGTH));
		jobStatus.setEndTime(Instant.now());

		if(ok) {
			jobStatus.setLastSuccessStartTime(jobStatus.getStartTime());
			jobStatus.setLastSuccessEndTime(jobStatus.getEndTime());
		}

		jobStatus = saveJobStatus(jobStatus);
		
		Ivy.log().info(new MessageFormatMessage("Job ''{0}'' started at ''{1}'' and ended at ''{2}'' with status ''{3}'', additionalJobData: ''{4}''",
				jobStatus.getName(),
				DateService.get().toDefaultString(jobStatus.getStartTime()),
				DateService.get().toDefaultString(jobStatus.getEndTime()),
				jobStatus.getRunStatus(),
				jobStatus.getAdditionalJobData()).getFormattedMessage());

		return jobStatus;
	}

	/**
	 * Register a Job so that it's description can be found by the job name.
	 *
	 * <p>
	 * Note: it is safe to call this from static code.
	 * </p>
	 *
	 * @param jobDescription
	 */
	public static void registerJobDescription(JobDescription jobDescription) {
		jobRepository.put(jobDescription.getJobName(), jobDescription);
	}

	/**
	 * Register a Job so that it's description can be found by the job name.
	 *
	 * <p>
	 * Note: it is safe to call this from static code.
	 * </p>
	 *
	 * @param jobName
	 * @param jobFunction
	 * @return
	 */
	public JobService registerJobDescription(String jobName, Function<JobStatus, ServiceResult> jobFunction) {
		jobRepository.put(jobName, JobDescription.create(jobName, jobFunction));
		return this;
	}

	/**
	 * Find a {@link JobDescription} registered before.
	 *
	 * @param jobName
	 * @return
	 */
	public JobDescription findJobDescription(String jobName) {
		return jobRepository.get(jobName);
	}

	/**
	 * Helper to pack additional data as a {@link String}.
	 *
	 * @param additionalData
	 * @return
	 */
	public String packAdditionalData(Object additionalData) {
		try {
			return MAPPER.writeValueAsString(additionalData);
		} catch (JsonProcessingException e) {
			throw BpmError
			.create(OK_MESSAGE).withMessage("Error packing additional data of object: %s".formatted(additionalData))
			.withCause(e)
			.build();
		}
	}

	/**
	 * Helper to unpack additional data object from a {@link String}.
	 *
	 * @param <T>
	 * @param additionalData
	 * @param type
	 * @return
	 */
	public <T> T unpackAdditionalData(String additionalData, Class<T> type) {
		try {
			return MAPPER.readValue(additionalData, type);
		} catch (JsonProcessingException e) {
			throw BpmError
			.create(OK_MESSAGE).withMessage("Error unpacking additional data '%s' of type: %s".formatted(additionalData, type))
			.withCause(e)
			.build();
		}
	}

	/**
	 * Represent the technical information needed for a Job.
	 */
	public static class JobDescription {
		private String jobName;
		private Function<JobStatus, ServiceResult> jobFunction;

		private JobDescription() {
		}

		public static JobDescription create(String jobName, Function<JobStatus, ServiceResult> jobFunction) {
			JobDescription jobDescription = new JobDescription();
			jobDescription.setJobName(jobName);
			jobDescription.setJobFunction(jobFunction);
			return jobDescription;
		}

		/**
		 * @return the jobName
		 */
		public String getJobName() {
			return jobName;
		}

		/**
		 * @param jobName the jobName to set
		 */
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		/**
		 * @return the jobFunction
		 */
		public Function<JobStatus, ServiceResult> getJobFunction() {
			return jobFunction;
		}

		/**
		 * @param jobFunction the jobFunction to set
		 */
		public void setJobFunction(Function<JobStatus, ServiceResult> jobFunction) {
			this.jobFunction = jobFunction;
		}
	}
}
