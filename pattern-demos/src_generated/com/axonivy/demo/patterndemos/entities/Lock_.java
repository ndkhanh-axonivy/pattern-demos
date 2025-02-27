package com.axonivy.demo.patterndemos.entities;

import java.time.Instant;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Lock.class)
public abstract class Lock_ extends com.axonivy.utils.persistence.beans.AuditableIdEntity_ {

	public static volatile SingularAttribute<Lock, String> name;
	public static volatile SingularAttribute<Lock, Instant> validUntil;
	public static volatile SingularAttribute<Lock, Boolean> locked;

	public static final String NAME = "name";
	public static final String VALID_UNTIL = "validUntil";
	public static final String LOCKED = "locked";

}

