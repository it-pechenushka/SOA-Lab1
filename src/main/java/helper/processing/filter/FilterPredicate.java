package helper.processing.filter;

import exception.InvalidParamsException;
import lombok.SneakyThrows;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class FilterPredicate<T extends Comparable<T>> implements CommonFilterPredicate {
    private final String fieldName;
    private final FilterType filterType;
    private final T fieldValue;

    public FilterPredicate(String fieldName, FilterType filterType, T fieldValue) {
        this.fieldName = fieldName;
        this.filterType = filterType;
        this.fieldValue = fieldValue;
    }

    @SneakyThrows
    public Predicate getQueryFilterPredicate(CriteriaBuilder criteriaBuilder, Path<?> root){
        switch (filterType){
            case LESS:
                return criteriaBuilder.lessThan(root.get(fieldName), fieldValue);
            case GREATER:
                return criteriaBuilder.greaterThan(root.get(fieldName), fieldValue);
            case EQUALS:
                return criteriaBuilder.equal(root.get(fieldName), fieldValue);
            default:
                throw new InvalidParamsException("Unexpected predicate type: " + filterType);
        }
    }
}
