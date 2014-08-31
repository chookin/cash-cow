package chookin.stock.orm.repository;

import chookin.stock.orm.domain.HistoryDataEntity;
import chookin.stock.orm.domain.HistoryDataEntityPK;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface HistoryDataRepository extends CrudRepository<HistoryDataEntity, HistoryDataEntityPK>{
}
