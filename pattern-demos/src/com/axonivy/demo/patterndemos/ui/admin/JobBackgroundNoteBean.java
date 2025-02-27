package com.axonivy.demo.patterndemos.ui.admin;

import java.time.Duration;

import org.apache.logging.log4j.message.MessageFormatMessage;

import com.axonivy.demo.patterndemos.service.JobService;
import com.axonivy.demo.patterndemos.service.UiService;

import ch.ivyteam.ivy.environment.Ivy;

public class JobBackgroundNoteBean {
	private String jobName;
	private String additionalJobData;
	private boolean execute;
	private boolean locked;

	public JobBackgroundNoteBean(String jobName, String additionalJobData) {
		this.jobName = jobName;
		this.additionalJobData = additionalJobData;
		locked = JobService.get().isLocked(jobName);
	}

	public void startJob() {
		Ivy.log().info(new MessageFormatMessage("Manually starting job ''{0}'' with additional job data: ''{1}''", jobName, additionalJobData).getFormattedMessage());
	}

	public void unlockJob() {
		Ivy.log().info(new MessageFormatMessage("Manually unlocking job ''{0}''", jobName).getFormattedMessage());
		boolean unlocked = JobService.get().forceUnlock(jobName, Duration.ofSeconds(10));
		if(!unlocked) {
			UiService.get().createFacesMessageCmsError("/Dialogs/at/ac/fwf/panda/core/ui/admin/JobBackgroundNote/unlockError", jobName);
		}
		locked = JobService.get().isLocked(jobName);
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
	 * @return the additionalJobData
	 */
	public String getAdditionalJobData() {
		return additionalJobData;
	}

	/**
	 * @param additionalJobData the additionalJobData to set
	 */
	public void setAdditionalJobData(String additionalJobData) {
		this.additionalJobData = additionalJobData;
	}

	/**
	 * @return the execute
	 */
	public boolean isExecute() {
		return execute;
	}

	/**
	 * @param execute the execute to set
	 */
	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
