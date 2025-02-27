package com.axonivy.demo.patterndemos.dao;

import com.axonivy.demo.patterndemos.entities.JobStatus;
import com.axonivy.demo.patterndemos.entities.JobStatus_;
import com.axonivy.utils.persistence.dao.AuditableIdDAO;
import com.axonivy.utils.persistence.dao.CriteriaQueryContext;


public class JobStatusDAO extends AuditableIdDAO<JobStatus_, JobStatus> implements BaseDAO {
	private static final JobStatusDAO INSTANCE = new JobStatusDAO();

	public static JobStatusDAO get() {
		return INSTANCE;
	}

	@Override
	protected Class<JobStatus> getType() {
		return JobStatus.class;
	}

	/**
	 * Find a {@link JobStatus} by it's name.
	 *
	 * @param name
	 * @return
	 */
	public JobStatus findByName(String name) {
		JobStatus result = null;
		try(CriteriaQueryContext<JobStatus> query = initializeQuery();){
			query.q.where(query.c.equal(query.r.get(JobStatus_.name), name));
			result = forceSingleResult(findByCriteria(query));
		}
		return result;
	}

}

