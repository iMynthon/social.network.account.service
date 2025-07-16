package social.network.account.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import social.network.account.aop.Loggable;
import social.network.account.dto.request.AccountRequest;
import social.network.account.dto.response.AccountResponse;
import social.network.account.dto.response.SearchAccountResponse;
import social.network.account.exception.EntityNotFoundException;
import social.network.account.exception.JsonParserRegistrationException;
import social.network.account.filter.AccountSearchDto;
import social.network.account.mapper.AccountMapper;
import social.network.account.model.Account;
import social.network.account.model.kafka.BlockDeleteEvent;
import social.network.account.model.kafka.RegistrationEvent;
import social.network.account.repository.AccountRepository;
import social.network.account.repository.specification.AccountSpecification;
import social.network.account.utils.SecurityUtils;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static social.network.account.dto.response.StringResponse.*;
import static social.network.account.utils.CaffeineCacheNames.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Loggable
@CacheConfig(cacheNames = "springCacheManager")
public class AccountService {

    private final AccountRepository repository;
    private final KafkaSendService sendService;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;
    private final ApplicationContext context;
    private ReentrantLock reentrantLock = new ReentrantLock(true);

    @Cacheable(value = ACCOUNT_ME,keyGenerator = "accountId")
    public AccountResponse findAccountById(UUID userId){
        return accountMapper.accountToResponse(context.getBean(AccountService.class).findById(userId));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ACCOUNT_ME,keyGenerator = "accountId"),
            @CacheEvict(value = ACCOUNT_ALL,keyGenerator = "accountId")
    })
    public String delete(){
        UUID accountId = SecurityUtils.accountId();
        repository.setIsDeleted(accountId,true);
        sendService.sendMessage(new BlockDeleteEvent(accountId,"delete",true));
        return DELETE_MESSAGE;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ACCOUNT_ME,keyGenerator = "accountId"),
            @CacheEvict(value = ACCOUNT_ALL,keyGenerator = "accountId")
    })
    public String block(UUID userId){
        repository.setIsBlocked(userId,true);
        sendService.sendMessage(new BlockDeleteEvent(userId,"block",true));
        return BLOCK_MESSAGE;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ACCOUNT_ME,keyGenerator = "accountId"),
            @CacheEvict(value = ACCOUNT_ALL,keyGenerator = "accountId")
    })
    public String unblock(UUID userId){
        repository.setIsBlocked(userId,false);
        sendService.sendMessage(new BlockDeleteEvent(userId,"block",false));
        return UNBLOCK_MESSAGE;
    }
    @Transactional(readOnly = true)
    @Cacheable(value = ACCOUNT_ALL,keyGenerator = "accountId")
    public SearchAccountResponse findAll(Pageable pageable){
        return accountMapper.listAccountToListResponse(repository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ACCOUNT_SEARCH_FILTER,keyGenerator = "searchFilter")
    public SearchAccountResponse findAllFilter(AccountSearchDto filter, Pageable pageable){
        return accountMapper.listAccountToListResponse(
                repository.findAll(AccountSpecification.withFilter(filter),pageable));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsFindArrayIds(UUID[] ids){
        UUID userId = SecurityUtils.accountId();
        return repository.findAllById(Arrays.asList(ids))
                .stream().filter(account -> !account.getId().equals(userId))
                .map(accountMapper::accountToResponse).toList();
    }

    @Cacheable(value = ACCOUNT_ME,keyGenerator = "accountId")
    public AccountResponse findMeAccount(){
        return accountMapper.accountToResponse(
                context.getBean(AccountService.class).findById(SecurityUtils.accountId()));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ACCOUNT_ME,keyGenerator = "accountId"),
            @CacheEvict(value = ACCOUNT_ALL,keyGenerator = "accountId")
    })
    public AccountResponse update(AccountRequest request){
        UUID id = SecurityUtils.accountId();
        Account updateAccount = accountMapper.requestToAccount(request);
        updateAccount.setPhone(updateAccount.getPhone().isBlank() ? null : updateAccount.getPhone());
        Account exists = findById(id);
        accountMapper.update(exists,updateAccount);
        return accountMapper.accountToResponse(repository.save(exists));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Caching(evict = {
            @CacheEvict(value = ACCOUNT_ME,keyGenerator = "accountId"),
            @CacheEvict(value = ACCOUNT_ALL,keyGenerator = "accountId")
    })
    public void registeredSave(String message){
        try {
            AccountRequest request = objectMapper.readValue(message, AccountRequest.class);
            Account account = accountMapper.requestToAccount(request);
            account.setIsBlocked(false);
            account.setIsDeleted(false);
            account.setIsOnline(true);
            repository.save(account);
            sendService.sendMessage(new RegistrationEvent(account.getId(),"registration",account.getEmail()));
        } catch (JsonProcessingException je){
            throw new JsonParserRegistrationException("Json parser exception -> AccountRequest - " + je);
        }

    }

    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        reentrantLock.lock();
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Account c id:{%s} - не найден", id)
                    ));
        } finally {
            reentrantLock.unlock();
        }
    }

}
