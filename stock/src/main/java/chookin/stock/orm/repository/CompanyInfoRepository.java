package chookin.stock.orm.repository;

import chookin.stock.orm.domain.CompanyInfoEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface CompanyInfoRepository extends CrudRepository<CompanyInfoEntity, String>{
}
