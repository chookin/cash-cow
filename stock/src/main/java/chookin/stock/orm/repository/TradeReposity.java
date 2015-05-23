package chookin.stock.orm.repository;

import chookin.stock.orm.domain.HistoryDayDetailEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface TradeReposity extends CrudRepository<HistoryDayDetailEntity, Long>{
}
