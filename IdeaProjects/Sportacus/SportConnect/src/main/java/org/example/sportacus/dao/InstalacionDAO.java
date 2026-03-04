package org.example.sportacus.dao;

import org.example.sportacus.model.Instalacion;
import org.example.sportacus.util.HibernateUtil;
import org.hibernate.Session;

public class InstalacionDAO {

    public Instalacion findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Instalacion.class, id);
        }
    }

    public Instalacion findFirst() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Instalacion i WHERE i.activa = true ORDER BY i.id ASC", Instalacion.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}
