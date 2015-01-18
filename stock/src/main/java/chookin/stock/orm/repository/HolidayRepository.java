package chookin.stock.orm.repository;

import chookin.stock.orm.domain.HolidayEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zhuyin on 1/18/15.
 */
public interface HolidayRepository extends CrudRepository<HolidayEntity, String> {
}
