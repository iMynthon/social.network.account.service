package social.network.account.mapper;
import org.mapstruct.*;
import org.springframework.data.domain.*;
import social.network.account.dto.request.AccountRequest;
import social.network.account.dto.response.AccountResponse;
import social.network.account.dto.response.CustomPageable;
import social.network.account.dto.response.SearchAccountResponse;
import social.network.account.model.Account;
import java.util.List;


@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    AccountResponse accountToResponse(Account account);

    Account requestToAccount(AccountRequest request);

    default SearchAccountResponse  listAccountToListResponse(Page<Account> accountList){
        List<AccountResponse> responseList = accountList.stream()
                .map(this::accountToResponse).toList();
        return SearchAccountResponse.builder()
                .totalPages(accountList.getTotalPages())
                .totalElements(accountList.getTotalElements())
                .pageable(createPageable(accountList.getPageable()))
                .size(accountList.getSize())
                .content(responseList)
                .number(accountList.getNumber())
                .sort(createSort(accountList.getPageable()))
                .numberOfElements(accountList.getNumberOfElements())
                .last(accountList.isLast())
                .first(accountList.isFirst())
                .empty(accountList.isEmpty())
                .build();
    }

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "regDate",ignore = true)
    void update(@MappingTarget Account destination, Account root);

    default CustomPageable.CustomSort createSort(Pageable pageable){
        return CustomPageable.CustomSort.builder()
                .sorted(pageable.getSort().isSorted())
                .empty(pageable.getSort().isEmpty())
                .unsorted(pageable.getSort().isUnsorted())
                .build();
    }

    default CustomPageable createPageable(Pageable pageable){
        return CustomPageable.builder()
                .paged(pageable.isPaged())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .offset(pageable.getOffset())
                .sort(createSort(pageable))
                .unpaged(pageable.isUnpaged())
                .build();
    }
}
