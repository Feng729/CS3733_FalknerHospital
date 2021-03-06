package service;


import model.HospitalService;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class HospitalServiceService extends AbstractService<HospitalService> {
    @Override
    public HospitalService find(Long id) {
        EntityManager manager = this.managerFactory.createEntityManager();
        HospitalService temp = manager.find(HospitalService.class, id);
        manager.close();
        return temp;
    }

    public HospitalService findHospitalServiceByName(String name) {
        EntityManager manager = this.managerFactory.createEntityManager();
        try {
            HospitalService temp = (HospitalService) manager.createQuery(
                    "SELECT s FROM HospitalService s WHERE s.name LIKE :name")
                    .setParameter("name", name)
                    .setMaxResults(1).getSingleResult();
            manager.close();
            return temp;
        } catch (NoResultException e) {
            manager.close();
            return null;
        }
    }

    public List<HospitalService> getAllServices() {
        EntityManager manager = this.managerFactory.createEntityManager();
        List<HospitalService> temp = manager.createQuery("from HospitalService", HospitalService.class)
                .getResultList();
        manager.close();
        return temp;
    }

    public List<HospitalService> search(String s) {
        EntityManager manager = managerFactory.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(manager);
        manager.getTransaction().begin();
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(HospitalService.class).get();
        org.apache.lucene.search.Query query = qb.keyword().onFields("title").matching(s).createQuery();
        javax.persistence.Query JPAQuery = fullTextEntityManager.createFullTextQuery(query, HospitalService.class);

        List<HospitalService> result = JPAQuery.getResultList();
        manager.getTransaction().commit();
        manager.close();
        return result;
    }

}
