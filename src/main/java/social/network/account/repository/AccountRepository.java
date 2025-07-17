package social.network.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import social.network.account.model.Account;
import social.network.account.repository.projections.IdProjections;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {



    Optional<IdProjections> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE accounts SET is_deleted = :isDeleted WHERE id = :id", nativeQuery = true)
    void setIsDeleted(@Param("id") UUID id, @Param("isDeleted") boolean isDeleted);

    @Modifying
    @Query(value = "UPDATE accounts SET is_blocked = :isBlocked WHERE id = :id", nativeQuery = true)
    void setIsBlocked(@Param("id") UUID id, @Param("isBlocked") boolean isBlocked);

    @Modifying
    @Query(value = "UPDATE accounts SET is_online = :isOnline WHERE id = :id",nativeQuery = true)
    void setIsOnline(@Param("id") UUID id,@Param("isOnline") boolean isOnline);
}
