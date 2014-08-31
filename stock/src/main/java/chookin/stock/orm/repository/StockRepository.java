package chookin.stock.orm.repository;

import chookin.stock.orm.domain.StockEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/29/14.
 */
public interface StockRepository extends CrudRepository<StockEntity, String> {
}
