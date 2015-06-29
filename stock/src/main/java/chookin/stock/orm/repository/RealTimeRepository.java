package chookin.stock.orm.repository;

import chookin.stock.orm.domain.RealtimeEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/29/14.
 */
public interface RealTimeRepository extends CrudRepository<RealtimeEntity, String> {
}
