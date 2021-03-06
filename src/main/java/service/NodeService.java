package service;


import model.Edge;
import model.Node;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeService extends AbstractService<Node> {
    @Override
    public Node find(Long id) {
        EntityManager manager = this.managerFactory.createEntityManager();
        Node temp = manager.find(Node.class, id);
        return temp;
    }

    public Set<Node> neighbors(Long id) {
        EntityManager manager = this.managerFactory.createEntityManager();
        List<Edge> edges = manager.createQuery("SELECT e FROM Edge e WHERE e.start.id = :id OR e.end.id = :id", Edge.class)
                .setParameter("id", id)
                .getResultList();
        Set<Node> startNeighbors = edges.stream().map(Edge::getStart).collect(Collectors.toSet());
        Set<Node> endNeighbors = edges.stream().map(Edge::getEnd).collect(Collectors.toSet());
        startNeighbors.addAll(endNeighbors);
        startNeighbors.remove(find(id));
        manager.close();
        return startNeighbors;
    }

    public Node findNodeByName(String name) {
        EntityManager manager = this.managerFactory.createEntityManager();
        try {
            Node temp = manager.createQuery(
                    "SELECT n FROM Node n WHERE n.name LIKE :name", Node.class)
                    .setParameter("name", name)
                    .setMaxResults(1).getSingleResult();
            manager.close();
            return temp;
        } catch (NoResultException e) {
            manager.close();
            return null;
        }
    }

    public Node findNodeByName(String name, int floor) {
        EntityManager manager = this.managerFactory.createEntityManager();
        return manager.createQuery("SELECT n FROM Node n WHERE n.name LIKE :name AND n.location.floor = :floor", Node.class)
                .setParameter("name", name)
                .setParameter("floor", floor)
                .getSingleResult();
    }

    public List<Node> findNodeIntersectionByFloor(int floor) {
        EntityManager manager = this.managerFactory.createEntityManager();
        List<Node> temp = manager.createQuery(
                "SELECT n FROM Node n WHERE n.name LIKE :name", Node.class)
                .setParameter("name", "intersection" + floor + "%")
                .getResultList();
        manager.close();
        return temp;
    }

    public List<Node> getAllNodes() {
        EntityManager manager = this.managerFactory.createEntityManager();
        List<Node> temp = manager.createQuery("from Node", Node.class)
                .getResultList();
        manager.close();
        return temp;
    }

    public List<Node> getNodesByFloor(int floor) {
        EntityManager manager = this.managerFactory.createEntityManager();
        List<Node> temp = manager.createQuery("SELECT n FROM Node n," +
                " Coordinate c WHERE n.location.id = c.id AND " +
                "c.floor = :floor", Node.class)
                .setParameter("floor", floor)
                .getResultList();
        manager.close();
        return temp;
    }

    public List<Node> getElevatorNodes() {
        EntityManager manager = this.managerFactory.createEntityManager();
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Node> elevatorCriteria = builder.createQuery(Node.class);
        Root<Node> root = elevatorCriteria.from(Node.class);
        elevatorCriteria.where(
                builder.like(
                        builder.lower(
                                root.get("name")
                        ),
                        "%elevator%"
                )
        );
        List<Node> nodes = manager.createQuery(elevatorCriteria).getResultList();
        System.out.println(nodes);
        return nodes;
    }

    public List<Node> search(String s) {
        EntityManager manager = managerFactory.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(manager);
        manager.getTransaction().begin();
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Node.class).get();
        org.apache.lucene.search.Query query = qb.keyword().onFields("name").matching(s).createQuery();
        javax.persistence.Query JPAQuery = fullTextEntityManager.createFullTextQuery(query, Node.class);

        List<Node> result = JPAQuery.getResultList();
        manager.getTransaction().commit();
        manager.close();
        return result;
    }
}