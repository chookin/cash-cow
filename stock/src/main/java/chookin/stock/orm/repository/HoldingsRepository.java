package chookin.stock.orm.repository;

import chookin.stock.orm.domain.HoldingsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhuyin on 6/15/15.
 */
public interface HoldingsRepository extends CrudRepository<HoldingsEntity, Long> {
    List<HoldingsEntity> findByValid(boolean valid);
}
