package com.axonivy.demo.patterndemos.entities;

import com.axonivy.demo.patterndemos.enums.JobRunStatus;
import java.time.Instant;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(JobStatus.class)
public abstract class JobStatus_ extends com.axonivy.utils.persistence.beans.AuditableIdEntity_ {

	public static volatile SingularAttribute<JobStatus, String> additionalJobData;
	public static volatile SingularAttribute<JobStatus, Instant> lastSuccessStartTime;
	public static volatile SingularAttribute<JobStatus, String> jobData;
	public static volatile SingularAttribute<JobStatus, String> name;
	public static volatile SingularAttribute<JobStatus, Instant> startTime;
	public static volatile SingularAttribute<JobStatus, Instant> endTime;
	public static volatile SingularAttribute<JobStatus, String> message;
	public static volatile SingularAttribute<JobStatus, JobRunStatus> runStatus;
	public static volatile SingularAttribute<JobStatus, Instant> lastSuccessEndTime;

	public static final String ADDITIONAL_JOB_DATA = "additionalJobData";
	public static final String LAST_SUCCESS_START_TIME = "lastSuccessStartTime";
	public static final String JOB_DATA = "jobData";
	public static final String NAME = "name";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String MESSAGE = "message";
	public static final String RUN_STATUS = "runStatus";
	public static final String LAST_SUCCESS_END_TIME = "lastSuccessEndTime";

}

