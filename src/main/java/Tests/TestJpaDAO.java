package Tests;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class TestJpaDAO<T extends Serializable> {

	@PersistenceContext
	private EntityManager entityManager;

	public void create(final T entity) {
		entityManager.persist(entity);
	}
}
