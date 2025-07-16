package social.network.account.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import social.network.account.filter.AccountSearchDto;
import social.network.account.model.Account;
import social.network.account.utils.SecurityUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public interface AccountSpecification {

    String BIRTH_DATE = "birthDate";

    static Specification<Account> withFilter(AccountSearchDto filter) {
        return Specification.where(byId(filter.getIds()))
                .and(byAuthor(filter.getAuthor()))
                .and(byFirstName(filter.getFirstName()))
                .and(byLastName(filter.getLastName()))
                .and(byBirthDateFromAndTo(filter.getBirthDateFrom(),filter.getBirthDateTo()))
                .and(byCity(filter.getCity()))
                .and(byCountry(filter.getCountry()))
                .and(byIsBlocked(filter.getIsBlocked()))
                .and(byIsDeleted(filter.getIsDeleted()))
                .and(byAgeRange(filter.getAgeTo(), filter.getAgeFrom())
                        .and(notCurrentUser()));
    }

    static Specification<Account> notCurrentUser(){
        return (root, query, cb) -> cb.notEqual(root.get("id"), SecurityUtils.accountId());
    }

    private static Specification<Account> byId(UUID[] ids) {
        return (root, query, cb) -> {
            if (ids == null || ids.length == 0) {
                return null;
            }
            return root.get("id").in((Object[]) ids);
        };
    }

    private static Specification<Account> byAuthor(String author) {
        return (root, query, cb) -> {
            if (author == null || author.isBlank()) {
                return null;
            }
            return cb.or(cb.like(cb.lower(root.get("firstName")),"%" + author.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("lastName")),"%" + author.toLowerCase() + "%"));
        };
    }

    private static Specification<Account> byFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    private static Specification<Account> byLastName(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("lastName")),"%" + lastName.toLowerCase() + "%");
        };
    }

    static Specification<Account> byBirthDateFromAndTo(Instant birthDateFrom, Instant birthDateTo) {
        return (root, query, cb) -> {
            if (birthDateFrom == null && birthDateTo == null) {
                return null;
            }
            LocalDate fromDate = birthDateFrom != null
                    ? birthDateFrom.atZone(ZoneId.systemDefault()).toLocalDate()
                    : null;
            LocalDate toDate = birthDateTo != null
                    ? birthDateTo.atZone(ZoneId.systemDefault()).toLocalDate()
                    : null;
            if (fromDate != null && toDate != null) {
                return cb.between(root.get(BIRTH_DATE), fromDate, toDate);
            } else if (toDate != null) {
                return cb.lessThanOrEqualTo(root.get(BIRTH_DATE), toDate);
            } else {
                return cb.greaterThanOrEqualTo(root.get(BIRTH_DATE), fromDate);
            }
        };
    }

    private static Specification<Account> byCity(String city) {
        return (root, query, cb) -> {
            if (city == null) {
                return null;
            }
            return cb.like(root.get("city"), city);
        };
    }

    private static Specification<Account> byCountry(String country) {
        return (root, query, cb) -> {
            if (country == null) {
                return null;
            }
            return cb.like(root.get("country"), country);
        };
    }

    private static Specification<Account> byIsBlocked(Boolean isBlocked) {
        return (root, query, cb) -> {
            if (isBlocked == null) {
                return null;
            }
            return cb.equal(root.get("isBlocked"), isBlocked);
        };
    }

    private static Specification<Account> byIsDeleted(Boolean isDeleted) {
        return (root, query, cb) -> {
            if (isDeleted == null) {
                return null;
            }
            return cb.equal(root.get("isDeleted"), isDeleted);
        };
    }

    private static Specification<Account> byAgeRange(Integer ageTo, Integer ageFrom) {
        return (root, query, cb) -> {
            if (ageFrom == null && ageTo == null) {
                return null;
            }
            LocalDate currentDate = LocalDate.now();
            LocalDate minBirthDate = ageFrom != null ? currentDate.minusYears(ageFrom) : null;
            LocalDate maxBirthDate = ageTo != null ? currentDate.minusYears(ageTo) : null;
            if (ageFrom != null && ageTo != null) {
                return cb.between(root.get(BIRTH_DATE), maxBirthDate, minBirthDate);
            } else if (ageFrom != null) {
                return cb.lessThanOrEqualTo(root.get(BIRTH_DATE), minBirthDate);
            } else {
                return cb.greaterThanOrEqualTo(root.get(BIRTH_DATE), maxBirthDate);
            }
        };
    }


}
