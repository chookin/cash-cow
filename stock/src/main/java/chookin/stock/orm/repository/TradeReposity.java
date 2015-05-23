package chookin.stock.orm.repository;

import chookin.stock.orm.domain.TradeEntity;
import chookin.stock.orm.domain.TradeEntityPK;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface TradeReposity extends CrudRepository<TradeEntity, TradeEntityPK>{
}
