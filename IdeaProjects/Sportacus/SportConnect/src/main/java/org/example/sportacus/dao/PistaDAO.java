package org.example.sportacus.dao;

import org.example.sportacus.model.Pista;
import org.example.sportacus.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class PistaDAO {

    public void save(Pista pista) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(pista);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void update(Pista pista) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery(
                "UPDATE Pista p SET p.nombre = :nombre, p.deporte = :deporte, " +
                "p.tipoSuperficie = :superficie, p.precioHora = :precio, " +
                "p.cubierta = :cubierta, p.iluminacion = :iluminacion " +
                "WHERE p.id = :id")
                .setParameter("nombre", pista.getNombre())
                .setParameter("deporte", pista.getDeporte())
                .setParameter("superficie", pista.getTipoSuperficie())
                .setParameter("precio", pista.getPrecioHora())
                .setParameter("cubierta", pista.isCubierta())
                .setParameter("iluminacion", pista.isIluminacion())
                .setParameter("id", pista.getId())
                .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("UPDATE Pista p SET p.activa = false WHERE p.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void hardDelete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Pista p = session.get(Pista.class, id);
            if (p != null) session.remove(p);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public List<Pista> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Pista p WHERE p.activa = true ORDER BY p.deporte.nombre, p.nombre",
                    Pista.class).list();
        }
    }

    public List<Pista> getPaginated(int page, int offset, HashMap<String, String> filtros) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("FROM Pista p WHERE p.activa = true");
            if (filtros != null) {
                for (String key : filtros.keySet()) {
                    if (key.equals("deporte"))
                        hql.append(" AND p.deporte.nombre LIKE :deporte");
                    else
                        hql.append(" AND p.").append(key).append(" LIKE :").append(key);
                }
            }
            hql.append(" ORDER BY p.deporte.nombre, p.nombre");

            Query<Pista> query = session.createQuery(hql.toString(), Pista.class);
            if (filtros != null) {
                for (HashMap.Entry<String, String> entry : filtros.entrySet()) {
                    query.setParameter(entry.getKey(), "%" + entry.getValue() + "%");
                }
            }
            query.setFirstResult((page - 1) * offset);
            query.setMaxResults(offset);
            return query.list();
        }
    }

    public long count(HashMap<String, String> filtros) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("SELECT COUNT(p) FROM Pista p WHERE p.activa = true");
            if (filtros != null) {
                for (String key : filtros.keySet()) {
                    if (key.equals("deporte"))
                        hql.append(" AND p.deporte.nombre LIKE :deporte");
                    else
                        hql.append(" AND p.").append(key).append(" LIKE :").append(key);
                }
            }
            Query<Long> query = session.createQuery(hql.toString(), Long.class);
            if (filtros != null) {
                for (HashMap.Entry<String, String> entry : filtros.entrySet()) {
                    query.setParameter(entry.getKey(), "%" + entry.getValue() + "%");
                }
            }
            return query.getSingleResult();
        }
    }

    /**
     * Devuelve pistas activas del deporte indicado que NO tienen reserva ni bloqueo
     * que solape con el intervalo [inicio, fin).
     */
    public List<Pista> getPistasDisponibles(Short deporteId, LocalDateTime inicio, LocalDateTime fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
                    FROM Pista p
                    WHERE p.activa = true
                      AND p.deporte.id = :deporteId
                      AND p.id NOT IN (
                          SELECT r.pista.id FROM Reserva r
                          WHERE r.estado <> 'CANCELADA'
                            AND r.inicio < :fin AND r.fin > :inicio
                      )
                      AND p.id NOT IN (
                          SELECT b.pista.id FROM BloqueoPista b
                          WHERE b.inicio < :fin AND b.fin > :inicio
                      )
                    ORDER BY p.nombre
                    """;
            return session.createQuery(hql, Pista.class)
                    .setParameter("deporteId", deporteId)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    public List<Pista> getByDeporte(Short deporteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Pista p WHERE p.activa = true AND p.deporte.id = :deporteId ORDER BY p.nombre",
                    Pista.class)
                    .setParameter("deporteId", deporteId)
                    .list();
        }
    }
}
