package org.example.sportacus.dao;

import org.example.sportacus.model.Usuario;
import org.example.sportacus.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    public void save(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void update(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(usuario);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<Usuario> findByEmailAndPassword(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Usuario u WHERE u.email = :email AND u.passwordHash = :password AND u.activo = true",
                    Usuario.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResultOptional();
        }
    }

    public Optional<Usuario> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Usuario u WHERE u.email = :email",
                    Usuario.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        }
    }

    public List<Usuario> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario u ORDER BY u.nombre", Usuario.class).list();
        }
    }

    public List<Usuario> getPaginated(int page, int offset) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario u ORDER BY u.nombre", Usuario.class)
                    .setFirstResult((page - 1) * offset)
                    .setMaxResults(offset)
                    .list();
        }
    }

    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult();
        }
    }

    public void updateUltimoLogin(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("UPDATE Usuario u SET u.ultimoLogin = :now WHERE u.id = :id")
                    .setParameter("now", java.time.LocalDateTime.now())
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("UPDATE Usuario u SET u.activo = false WHERE u.id = :id")
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
            Usuario u = session.get(Usuario.class, id);
            if (u != null) session.remove(u);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }
}
