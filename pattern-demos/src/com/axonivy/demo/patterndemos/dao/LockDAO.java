package com.axonivy.demo.patterndemos.dao;

import javax.persistence.criteria.Expression;

import com.axonivy.demo.patterndemos.entities.Lock;
import com.axonivy.demo.patterndemos.entities.Lock_;
import com.axonivy.utils.persistence.dao.AuditableIdDAO;
import com.axonivy.utils.persistence.dao.CriteriaQueryContext;

public class LockDAO extends AuditableIdDAO<Lock_, Lock> implements BaseDAO {
	private static final LockDAO INSTANCE = new LockDAO();

	public static LockDAO get() {
		return INSTANCE;
	}

	@Override
	protected Class<Lock> getType() {
		return Lock.class;
	}

	/**
	 * Find a {@link Lock} by it's name.
	 *
	 * @param type
	 * @return
	 */
	public Lock findByName(String type) {
		Lock lock = null;
		try (CriteriaQueryContext<Lock> ctx = initializeQuery()) {
			Expression<String> nameEx = getExpression(null, ctx.r, Lock_.name);

			ctx.where(ctx.c.equal(nameEx, type));
			lock = forceSingleResult(findByCriteria(ctx));
		}
		return lock;
	}
}
