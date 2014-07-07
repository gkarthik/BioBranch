package org.scripps.branch.repository;
//package org.scripps.branch.repository;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
//import javax.transaction.Transactional;
//
//import org.scripps.branch.entity.Attribute;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@Transactional
//public class AttributeRepositoryImpl implements AttributeRepository {
//	
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(AttributeRepositoryImpl.class);
//	
//	protected EntityManager em;
//	
//	@PersistenceContext
//	public void setEntityManager(EntityManager em) {
//		this.em = em;
//		LOGGER.debug("entity manager set from Impl!");
//	}
//	
//	public void getEntityManager(EntityManager em) {
//		this.em = em;
//	}
//	
//	@Override
//	public List<Attribute> findByFeatureUniqueId(String unique_id, String dataset) {
//		List<Attribute> atts = new ArrayList<Attribute>();
//		int counter = 0;
//		Attribute attributeObject = new Attribute();
//		try {
//			String query = "select A from Attribute A, Feature F where A.feature=F.id and F.unique_id='"
//					+ unique_id + "' and A.dataset='" + dataset + "'";
//			LOGGER.debug(query);
//			Query q = em.createQuery(query);
//			List<?> list = q.getResultList();
//			Iterator<?> it = list.iterator();
//
//			while (it.hasNext()) {
//				attributeObject = (Attribute) it.next();
//				atts.add(attributeObject);
//				counter++;
//				LOGGER.debug("Attribute Object" + attributeObject.toString());
//			}
//			LOGGER.debug("Counter =" + counter);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return atts;
//	}
//
//	@Override
//	public long count() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void delete(Long arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void delete(Attribute arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void delete(Iterable<? extends Attribute> arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void deleteAll() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean exists(Long arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public Iterable<Attribute> findAll(Iterable<Long> arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Attribute findOne(Long arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public <S extends Attribute> S save(S arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public void deleteAllInBatch() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void deleteInBatch(Iterable<Attribute> arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public List<Attribute> findAll(Sort arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void flush() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public Attribute saveAndFlush(Attribute arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Page<Attribute> findAll(Pageable arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Attribute> findAll() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public <S extends Attribute> List<S> save(Iterable<S> arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}