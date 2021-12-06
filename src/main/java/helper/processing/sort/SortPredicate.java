package helper.processing.sort;

import exception.InvalidParamsException;
import lombok.SneakyThrows;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class SortPredicate implements CommonSortPredicate {
    private final String fieldName;
    private final SortType sortType;

    public SortPredicate(String fieldName, SortType sortType) {
        this.fieldName = fieldName;
        this.sortType = sortType;
    }

    @SneakyThrows
    public Order getSortOrder(CriteriaBuilder criteriaBuilder, Path<?> root){
        switch (sortType){
            case UP:
                return criteriaBuilder.desc(root.get(fieldName));
            case DOWN:
                return criteriaBuilder.asc(root.get(fieldName));
            default:
                throw new InvalidParamsException("Unexpected sort predicate type: " + sortType);
        }
    }
}
