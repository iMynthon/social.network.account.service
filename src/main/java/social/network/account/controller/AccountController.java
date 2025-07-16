package social.network.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import social.network.account.aop.LoggableController;
import social.network.account.dto.request.AccountRequest;
import social.network.account.dto.response.AccountResponse;
import social.network.account.dto.response.SearchAccountResponse;
import social.network.account.filter.AccountSearchDto;
import social.network.account.service.AccountService;
import social.network.account.utils.CaffeineCacheNames;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@LoggableController
public class AccountController {

    private final AccountService accountService;
    private final CacheManager cacheManager;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public AccountResponse getAccountMe(){
        cacheManager.getCache(CaffeineCacheNames.ACCOUNT_ME);
        return accountService.findMeAccount();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public AccountResponse updateAccountMe(@RequestBody AccountRequest request){
        return accountService.update(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public String deleteAccountMe(){
        return accountService.delete();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/block/{id}")
    @PreAuthorize("isAuthenticated()")
    public String unblockAccount(@PathVariable UUID id){
        return accountService.unblock(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/block/{id}")
    @PreAuthorize("isAuthenticated()")
    public String blockAccount(@PathVariable UUID id){
        return accountService.block(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public SearchAccountResponse getAllAccounts(@PageableDefault Pageable pageable){
        return accountService.findAll(pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/searchByFilter")
    @PreAuthorize("isAuthenticated()")
    public SearchAccountResponse searchByFilterAccounts(Pageable pageable,@RequestBody AccountSearchDto filter){
        return accountService.findAllFilter(filter,pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/find")
    @PreAuthorize("isAuthenticated()")
    public List<AccountResponse> findAccountsIds(@RequestBody UUID[] ids){
        return accountService.getAccountsFindArrayIds(ids);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public AccountResponse findByIdAccount(@PathVariable UUID id){
        return accountService.findAccountById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public SearchAccountResponse searchAccount(@PageableDefault Pageable pageable, AccountSearchDto filter){
        return accountService.findAllFilter(filter,pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accountIds")
    @PreAuthorize("isAuthenticated()")
    public SearchAccountResponse accountsIds(@PageableDefault Pageable pageable, AccountSearchDto filter){
        return accountService.findAllFilter(filter,pageable);
    }
}
