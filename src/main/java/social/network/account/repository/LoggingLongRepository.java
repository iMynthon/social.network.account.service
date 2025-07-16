package social.network.account.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import social.network.account.model.log.Log;

@Repository
public interface LoggingLongRepository extends CrudRepository<Log,Integer> {
}
