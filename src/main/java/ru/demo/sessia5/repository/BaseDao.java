package ru.demo.sessia5.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.demo.sessia5.util.HibernateSession;

import java.util.List;

public class BaseDao<T> {
    private Class<T> clazz;
    public BaseDao(Class<T> clazz){this.clazz = clazz;}

    protected Session getCurrentSession() {return HibernateSession.getSessionFactory().getCurrentSession();}

    public void save(T entity){
        Session session = getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.persist(entity);
        transaction.commit();
        session.close();
    }

    public void update(T entity){
        Session session = getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.merge(entity);
        transaction.commit();
        session.close();
    }

    public void delete(T entity){
        Session session = getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.remove(entity);
        transaction.commit();
        session.close();
    }

    public T findById(int id){
        Session session = getCurrentSession();
        Transaction transaction = session.beginTransaction();
        T item = session.get(clazz, id);
        session.close();
        return item;
    }

    public void deleteById(int id){
        T entity = findById(id);
        if(entity != null) delete(entity);
    }

    public List<T> findAll(){
        Session session = getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<T> items = session.createQuery("FROM " + clazz.getName(), clazz).list();
        session.close();
        return items;
    }

}
