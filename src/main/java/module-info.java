module ru.demo.sessia5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.desktop;
    requires java.naming;


    opens ru.demo.sessia5 to javafx.fxml;
    exports ru.demo.sessia5;
    opens ru.demo.sessia5.model to org.hibernate.orm.core, javafx.base;
}