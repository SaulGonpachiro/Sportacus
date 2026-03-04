package org.example.sportacus.dao;

import org.example.sportacus.model.Deporte;
import org.example.sportacus.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DeporteDAO {

    public void save(Deporte deporte) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(deporte);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void delete(Short id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Deporte d = session.get(Deporte.class, id);
            if (d != null) {
                session.remove(d);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public List<Deporte> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Deporte d ORDER BY d.nombre", Deporte.class).list();
        }
    }

    public Deporte findById(Short id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Deporte.class, id);
        }
    }
}
