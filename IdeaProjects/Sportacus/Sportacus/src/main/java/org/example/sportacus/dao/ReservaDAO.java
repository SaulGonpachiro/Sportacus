package org.example.sportacus.dao;

import org.example.sportacus.model.Reserva;
import org.example.sportacus.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class ReservaDAO {

    public void save(Reserva reserva) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(reserva);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void update(Reserva reserva) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(reserva);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public List<Reserva> getByUsuario(Long usuarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Reserva r WHERE r.usuario.id = :uid ORDER BY r.inicio DESC",
                    Reserva.class)
                    .setParameter("uid", usuarioId)
                    .list();
        }
    }

    public List<Reserva> getProximasByUsuario(Long usuarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Reserva r WHERE r.usuario.id = :uid AND r.inicio >= :ahora AND r.estado <> 'CANCELADA' ORDER BY r.inicio ASC",
                    Reserva.class)
                    .setParameter("uid", usuarioId)
                    .setParameter("ahora", LocalDateTime.now())
                    .list();
        }
    }

    public List<Reserva> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Reserva r ORDER BY r.inicio DESC",
                    Reserva.class).list();
        }
    }

    public List<Reserva> getPaginated(int page, int offset) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Reserva r ORDER BY r.inicio DESC", Reserva.class)
                    .setFirstResult((page - 1) * offset)
                    .setMaxResults(offset)
                    .list();
        }
    }

    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(r) FROM Reserva r", Long.class).getSingleResult();
        }
    }

    public void cancelar(Long reservaId, String motivo) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery(
                    "UPDATE Reserva r SET r.estado = 'CANCELADA', r.motivoCancelacion = :motivo " +
                    "WHERE r.id = :id")
                    .setParameter("motivo", motivo != null ? motivo : "")
                    .setParameter("id", reservaId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void delete(Long reservaId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Reserva r = session.get(Reserva.class, reservaId);
            if (r != null) session.remove(r);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public boolean existeSolape(Long pistaId, LocalDateTime inicio, LocalDateTime fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(r) FROM Reserva r WHERE r.pista.id = :pistaId AND r.estado <> 'CANCELADA' AND r.inicio < :fin AND r.fin > :inicio",
                    Long.class)
                    .setParameter("pistaId", pistaId)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getSingleResult();
            return count > 0;
        }
    }

}
