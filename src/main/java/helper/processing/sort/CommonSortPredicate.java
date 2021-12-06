package helper.processing.sort;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

public interface CommonSortPredicate {
    Order getSortOrder(CriteriaBuilder criteriaBuilder, Path<?> root);
}
