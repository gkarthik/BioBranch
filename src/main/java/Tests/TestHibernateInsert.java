//package Tests;
//
//import javax.persistence.EntityManager;
////import javax.persistence.PersistenceContext;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class TestHibernateInsert {
//
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(TestHibernateInsert.class);
//
//	//
//	// @PersistenceUnit(unitName="NEW")
//	// EntityManagerFactory emf ;// =
//	// Persistence.createEntityManagerFactory("DEFAULTJPA");
//	//
//
//	// public static void main(String[] args)
//	// {
//	// Session session = HibernateUtil.getSessionFactory().openSession();
//	// session.beginTransaction();
//	//
//	// //Add new Employee object
//	// Test emp = new Test();
//	//
//	// emp.setName("Vyshakh");
//	//
//	//
//	//
//	// //Save the employee in database
//	// session.save(emp);
//	//
//	// //Commit the transaction
//	// session.getTransaction().commit();
//	// HibernateUtil.shutdown();
//	// }
//
//	public static void main(String args[]) {
//
//		TestHibernateInsert tt = new TestHibernateInsert();
//		tt.nmain();
//	}
//
//	@Transactional
//	// EntityManager em;
//	public void nmain() {
//
//		// @PersistenceUnit(unitName="NEW")
//		EntityManagerFactory emf = Persistence
//				.createEntityManagerFactory("DEFAULTJPA");
//		EntityManager em = emf.createEntityManager();
//
//		// /EntityManager em= emf.createEntityManager();
//
//		LOGGER.debug("Check the transaction", em);
//
//		em.getTransaction().begin();
//
//		Test T = new Test();
//		Test T2 = new Test();
//
//		for (int i = 0; i <= 2; i++) {
//
//			T.setName("Shreyas");
//		}
//		T2.setName("Vyshakh");
//		em.persist(T);
//		em.persist(T2);
//		em.getTransaction().commit();
//
//	}
//
//}
//
//// String hql =
//// "Insert into FeatureDB (unique_id,short_name,long_name,description) values("+result[0]+","+result[1]+","+result[2]+","+result[3]+")";
//// Query q = em.createQuery(hql);
//// int res= q.executeUpdate();
//// System.out.println("Rows affected: " + res);
//// fdb.setUnique_id((int) result[0]);
//// fdb.setShort_name((String) result[1]);
//// fdb.setLong_name((String) result[2]);
//// fdb.setDescription((String) result[3]);
//// em.persist(fdb);
//
//// em.getTransaction().commit();
//// em.close();
//// "insert into FeatureDB (unique_id,short_name,long_name,description)" +
//// "select geneid,symbol,full_name_from_nomenclature_authority,description from Homosapiens_Gene_Info";
//// //"+result[0]+","+result[1]+","+result[2]+","+result[3]+"";
//// List<?> geneids = geneid();
////
//// Iterator<?> it = geneids.iterator( );
//// FeatureDB fdb =new FeatureDB();
//// int res = 0;
//// while (it.hasNext( )) {
////
////
//// Object[] result = (Object[])it.next(); // Iterating through array object
//// System.out.println("ID: \t" + result[0]+"\tSymbol: " +
//// result[1]+"\tFullName: " + result[2]+"\tDescription: " + result[3]);
////
////
////
//// }
////
//// }
//
////
//// public static List<?> geneid(){
////
//// Query q=
//// em.createQuery("select geneid,symbol,full_name_from_nomenclature_authority,description from Homosapiens_Gene_Info");
//// return q.getResultList();
////
//// }
//
//// attributeObject.setId((int) result[7]);
//// attributeObject.setCol_index((int) result[8]);
//// attributeObject.setCreated((DateTime) result[9]);
//// attributeObject.setDataset((String) result[10]);
//// attributeObject.setName((String) result[11]);
//// attributeObject.setRelieff((float) result[12]);
//// attributeObject.setUpdated((DateTime) result[13]);
//
//// @ManyToOne(fetch = FetchType.LAZY)
//// @JoinColumn(name = "feature_id", insertable = false, updatable = false,
//// nullable = false)
//// private FeatureDB feature_id;
//
//// private Long feature_id;
////
//// public long getFeature_id() {
//// return feature_id;
//// }
////
//// public void setFeature_id(long feature_id) {
//// this.feature_id = featuredb.getId();
// // }
