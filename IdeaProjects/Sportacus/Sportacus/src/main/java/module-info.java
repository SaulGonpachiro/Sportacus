module org.example.sportacus {
    requires javafx.controls;
    requires javafx.fxml;

    // Hibernate 5
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.persistence;

    // Para que Hibernate pueda acceder a los modelos por reflexión
    opens org.example.sportacus.model to org.hibernate.orm.core, javafx.fxml;
    opens org.example.sportacus to javafx.fxml;
    opens org.example.sportacus.controller to javafx.fxml;

    exports org.example.sportacus;
    exports org.example.sportacus.controller;
    exports org.example.sportacus.model;
    exports org.example.sportacus.service;
    exports org.example.sportacus.dao;
    exports org.example.sportacus.util;
}
