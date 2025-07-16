package social.network.account.dto.response;

import lombok.Builder;

@Builder
public record CustomPageable(
       boolean paged,
       int pageNumber,
       int pageSize,
       long offset,
       CustomSort sort,
       boolean unpaged
) {
    @Builder
    public record CustomSort(boolean sorted, boolean empty, boolean unsorted){}
}
