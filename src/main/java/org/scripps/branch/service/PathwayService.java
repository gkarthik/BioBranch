package org.scripps.branch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.scripps.branch.entity.CPDB_Pathway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathwayService extends CPDB_Pathway {

	public static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("DEFAULTJPA");
	public static EntityManager em = emf.createEntityManager();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PathwayService.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getGenesOfPathways(String pathwayName) {

		int counter = 0;
		ArrayList aListObj = new ArrayList();

		String query = "select f.short_name,f.long_name, c.entrez_id,c.source_db"
				+ " from CPDB_Pathway p,Feature f"
				+ " where p.name = '"
				+ pathwayName
				+ "' and p.entrez_id =f.unique_id"
				+ "group by c.entrez_id";

		try {

			em.getTransaction().begin();
			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();

			while (it.hasNext()) {
				HashMap row = new HashMap();
				Object[] result = (Object[]) it.next();
				row.put("unique_id", result[2]);
				row.put("short_name", result[0]);
				row.put("long_name", result[1]);
				row.put("source", result[3]);
				aListObj.add(row);

				counter++;
				LOGGER.debug("Pathways" + aListObj.toString());
			}
			LOGGER.debug("Counter =" + counter);
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aListObj;
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList searchPathways(String name) {

		int counter = 0;
		ArrayList pathways = new ArrayList();

		String query = "select p from CPDB_Pathway p where p.name LIKE '%"
				+ name + "%' group by name";

		try {

			em.getTransaction().begin();
			Query q = em.createQuery(query);

			List<?> list = q.getResultList();

			Iterator<?> it = list.iterator();

			while (it.hasNext()) {

				pathways = (ArrayList) it.next();
				counter++;
				LOGGER.debug("Pathways" + pathways.toString());
			}
			LOGGER.debug("Counter =" + counter);
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pathways;
	}

}
