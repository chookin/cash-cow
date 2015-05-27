package chookin.stock.orm.repository;

import chookin.stock.orm.domain.CompanyEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chookin on 7/30/14.
 */
public interface CompanyRepository extends CrudRepository<CompanyEntity, String>{
}
