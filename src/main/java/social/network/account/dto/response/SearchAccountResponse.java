package social.network.account.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record SearchAccountResponse(
        int totalPages,
        long totalElements,
        CustomPageable pageable,
        int size,
        List<AccountResponse> content,
        int number,
        CustomPageable.CustomSort sort,
        int numberOfElements,
        boolean last,
        boolean first,
        boolean empty
) {
}
