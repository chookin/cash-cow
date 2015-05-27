package chookin.stock.orm.repository;

import chookin.stock.orm.domain.RealTimeEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/29/14.
 */
public interface RealTimeRepository extends CrudRepository<RealTimeEntity, String> {
}
