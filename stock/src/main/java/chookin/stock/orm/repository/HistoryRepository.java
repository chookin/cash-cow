package chookin.stock.orm.repository;

import chookin.stock.orm.domain.HistoryEntity;
import chookin.stock.orm.domain.HistoryEntityPK;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface HistoryRepository extends CrudRepository<HistoryEntity, HistoryEntityPK>{
}
