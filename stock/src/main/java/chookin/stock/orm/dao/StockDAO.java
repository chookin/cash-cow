package chookin.stock.orm.dao;

import chookin.stock.orm.domain.StockEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chookin on 7/28/14.
 */
public class StockDAO {
    public StockEntity findByCode(String stock_code){
        Session session = HibernateUtil.getSession();
        try{
            return (StockEntity)session.get(StockEntity.class, stock_code);
        }finally {
            session.close();
        }
    }

    public StockEntity findByName(String stock_name){
        Session session = HibernateUtil.getSession();
        try{
            Criteria criteria = session.createCriteria(StockEntity.class);
            criteria.add(Restrictions.eq("stockName", stock_name));
            return (StockEntity) criteria.uniqueResult();
        }finally {
            session.close();
        }
    }

    public Map<String, StockEntity> findAll() {
        Map<String, StockEntity> stocks = new TreeMap<String, StockEntity>();
        Session session = HibernateUtil.getSession();
        try{
            Criteria query = session.createCriteria(StockEntity.class);
            List list = query.list();
            for(int i = 0 ; i < list.size(); i++){
                StockEntity item = (StockEntity) list.get(i);
                stocks.put(item.getStockCode(), item);
            }
        }finally {
            session.close();
        }
        return stocks;
    }

    public void save(Map<String, StockEntity> stocks, boolean rewrite){
        Session session = HibernateUtil.getSession();
        try{
            session.beginTransaction();
            if(rewrite){
                session.createSQLQuery("truncate stock").executeUpdate();
                for(StockEntity item : stocks.values()){
                    session.save(item);
                }
            }else {
                Criteria query = session.createCriteria(StockEntity.class);
                List list = query.list();
                Map<String, StockEntity> stocksExist = new TreeMap<String, StockEntity>();
                for(int i = 0 ; i < list.size(); i++){
                    StockEntity item = (StockEntity) list.get(i);
                    stocksExist.put(item.getStockCode(), item);
                }
                for(StockEntity item : stocks.values()){
                    if(stocksExist.containsKey(item.getStockCode())){
                        continue;
                    }
                    session.save(item);
                }
            }
            session.getTransaction().commit();
        }finally {
            session.close();
        }
    }
}
