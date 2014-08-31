package chookin.stock.orm.dao;

import org.hibernate.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Created by chookin on 7/29/14.
 */
public class AbstractHibernateDAO<T, PK extends Serializable> {
    protected Class <T> entityClass = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    /**
     * Persist the newInstance object into database
     * @param newInstance
     */
    public void save(T newInstance){
        Session session = HibernateUtil.getSession();
        try{
                session.save(newInstance);
        }finally {
            session.close();
        }
    }
    public void save(Collection<T> items){
        Session session = HibernateUtil.getSession();
        try{
            session.beginTransaction();
            for(T item : items){
                session.save(item);
            }
            session.getTransaction().commit();
        }finally {
            session.close();
        }
    }
    /** Save changes made to a persistent object.  */
    public  void update(T transientObject){
        Session session = HibernateUtil.getSession();
        try{
            session.update(transientObject);
        }finally {
            session.close();
        }
    }
    /** Retrieve an object that was previously persisted to the database using
     *   the indicated id as primary key
     */
    public T read(PK id){
        Session session = HibernateUtil.getSession();
        try{
            return  (T)session.get(this.entityClass, id);
        }finally {
            session.close();
        }
    }

    /** Remove an object from persistent storage in the database */
    public void delete(T item){
        Session session = HibernateUtil.getSession();
        try{
            session.delete(item);
        }finally {
            session.close();
        }
    }

    public void delete(Collection<T> items){
        Session session = HibernateUtil.getSession();
        try{
            session.beginTransaction();
            for(T item : items){
                session.delete(item);
            }
            session.getTransaction().commit();
        }catch (RuntimeException e){
            session.getTransaction().rollback();
            throw e;
        }finally {
            session.close();
        }
    }

    public void deleteAll(){
        Session session = HibernateUtil.getSession();
        try{
            session.beginTransaction();
            session.createQuery(" delete from "+ this.entityClass)
                    .executeUpdate();
            session.getTransaction().commit();
        }catch (RuntimeException e){
            session.getTransaction().rollback();
            throw e;
        }finally {
            session.close();
        }
    }


    public List<T> findAll() {
        List<T> items = new ArrayList<T>();
        Session session = HibernateUtil.getSession();
        try{
            Criteria query = session.createCriteria(entityClass);
            List list = query.list();
            for(int i = 0 ; i < list.size(); i++){
                T item = (T) list.get(i);
                items.add(item);
            }
        }finally {
            session.close();
        }
        return items;
    }

    public void executeSql(String sql){
        Session session = HibernateUtil.getSession();
        try{
            SQLQuery query = session.createSQLQuery(sql);
            query.executeUpdate();
        }finally {
            session.close();
        }
    }

    public List findByHql(String hql){
        Session session = HibernateUtil.getSession();
        List result = null;
        try{
            Query query = session.createQuery(hql);
            result = query.list();
        }finally {
            session.close();
        }
        return result;
    }
}
