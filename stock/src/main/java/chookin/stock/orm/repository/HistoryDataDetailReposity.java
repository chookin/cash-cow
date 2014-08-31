package chookin.stock.orm.repository;

import chookin.stock.orm.domain.HistoryDataEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface HistoryDataDetailReposity  extends CrudRepository<HistoryDataEntity, Long>{
}
