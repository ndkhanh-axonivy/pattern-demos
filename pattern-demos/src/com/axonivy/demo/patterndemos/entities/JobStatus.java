package com.axonivy.demo.patterndemos.entities;

import java.io.IOException;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.slf4j.helpers.MessageFormatter;

import com.axonivy.demo.patterndemos.Constants;
import com.axonivy.demo.patterndemos.enums.JobRunStatus;
import com.axonivy.demo.patterndemos.service.JobService;
import com.axonivy.utils.persistence.beans.AuditableIdEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;

@Entity
@Table
public class JobStatus extends AuditableIdEntity {
	private static final long serialVersionUID = 1L;
	public static final int MAX_MESSAGE_LENGTH = 1024*1024;
//	private static final Logger LOG = LogService.get().getLogger();

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Column(length = Constants.LARGE_COLUMN_LENGTH)
	private String name;

	@Column
	@Enumerated(EnumType.STRING)
	private JobRunStatus runStatus;

	@Column
	private Instant startTime;

	@Column
	private Instant endTime;

	@Column
	private Instant lastSuccessStartTime;

	@Column
	private Instant lastSuccessEndTime;

	@Column(length = MAX_MESSAGE_LENGTH)
	@Lob
	private String message;

	@Column
	@Lob
	private String jobData;

	@Column
	@Lob
	private String additionalJobData;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the runStatus
	 */
	public JobRunStatus getRunStatus() {
		return runStatus;
	}

	/**
	 * @param runStatus the runStatus to set
	 */
	public void setRunStatus(JobRunStatus runStatus) {
		this.runStatus = runStatus;
	}

	/**
	 * @return the startTime
	 */
	public Instant getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Instant getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the lastSuccessStartTime
	 */
	public Instant getLastSuccessStartTime() {
		return lastSuccessStartTime;
	}

	/**
	 * @param lastSuccessStartTime the lastSuccessStartTime to set
	 */
	public void setLastSuccessStartTime(Instant lastSuccessStartTime) {
		this.lastSuccessStartTime = lastSuccessStartTime;
	}

	/**
	 * @return the lastSuccessEndTime
	 */
	public Instant getLastSuccessEndTime() {
		return lastSuccessEndTime;
	}

	/**
	 * @param lastSuccessEndTime the lastSuccessEndTime to set
	 */
	public void setLastSuccessEndTime(Instant lastSuccessEndTime) {
		this.lastSuccessEndTime = lastSuccessEndTime;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Additional data (parameters) for job start.
	 *
	 * String representation of job start data. It is in the responsibility of the job to understand/unmarshall this data.
	 *
	 * @return the additionalJobData might be <code>null</code>
	 */
	public String getAdditionalJobData() {
		return additionalJobData;
	}

	/**
	 * Additional data (parameters) for job start.
	 *
	 * String representation of job start data. It is in the responsibility of the job to understand/unmarshall this data.
	 *
	 * @param additionalJobData the additionalJobData to set
	 */
	public void setAdditionalJobData(String additionalJobData) {
		this.additionalJobData = additionalJobData;
	}

	/**
	 * Get additional job data by unpacking it's JSON representation.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> T getAdditionalJobData(Class<T> type) {
		return JobService.get().unpackAdditionalData(additionalJobData, type);
	}

	/**
	 * Set additional job data by packing it into it's JSON representation.
	 *
	 * @param data
	 */
	public void setAdditionalJobData(Object data) {
		additionalJobData = JobService.get().packAdditionalData(data);
	}


	/**
	 * Data the Job wants to remember for it's next run.
	 *
	 * Jobs will be started with the saved job status of the previous run.
	 *
	 * @return the jobData
	 */
	public String getJobData() {
		return jobData;
	}

	/**
	 * Data the Job wants to remember for it's next run.
	 *
	 * Jobs will be started with the saved job status of the previous run.
	 *
	 * @param jobData the jobData to set
	 */
	public void setJobData(String jobData) {
		this.jobData = jobData;
	}

	/**
	 * Extract the job's data.
	 *
	 * @param <T>
	 * @param jobDataClass type to expectjson coded in job's data
	 * @return
	 */
	public <T> T unpackFromJobData(Class<T> jobDataClass) {
		T result = null;
		String data = getJobData();
		if(data != null) {
			try {
				result = objectMapper.readerFor(jobDataClass).readValue(data);
			} catch (IOException e) {
				Ivy.log().error(MessageFormatter.format("Could not unpack JobData from JobStatus ''{0}'' {1}", name, e).getMessage());
//				LOG.error("Could not unpack JobData from JobStatus ''{0}'' with id: ''{1}'': {2}", e, name, id, data);
			}
		}
		return result;
	}

	/**
	 * Pack the job's data.
	 *
	 * A job can save data for a later run. It is up to the job to make sure, that the data can be serialized and desrialized to and from a json string.
	 *
	 * @param <T>
	 * @param jobData
	 */
	public <T> void packToJobData(T jobData) {
		String jobDataString = null;
		try {
			jobDataString = objectMapper.writeValueAsString(jobData);
			setJobData(jobDataString);
		} catch (JsonProcessingException e) {
			Ivy.log().error(MessageFormatter.format("Could not pack JobData to JobStatus ''{0}''. Leaving the previous message untouched. {1}", name, e).getMessage());
//			LOG.error("Could not pack JobData to JobStatus ''{0}'' with id: ''{1}''. Leaving the previous message untouched.", e, name, id);
		}
	}
}
