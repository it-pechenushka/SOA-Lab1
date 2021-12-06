package helper.processing.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public interface CommonFilterPredicate {
    Predicate getQueryFilterPredicate(CriteriaBuilder criteriaBuilder, Path<?> root);
}
