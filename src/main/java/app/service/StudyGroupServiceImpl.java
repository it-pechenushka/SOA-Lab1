package app.service;

import app.exception.InvalidParamsException;
import app.helper.processing.Filter;
import app.repository.GroupRepository;
import app.model.StudyGroup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudyGroupServiceImpl implements StudyGroupService {
    private final GroupRepository groupRepository;
    private final EntityManager em;

    public StudyGroupServiceImpl(GroupRepository groupRepository, EntityManager em) {
        this.groupRepository = groupRepository;
        this.em = em;
    }

    @Override
    @Transactional
    public List<StudyGroup> findAll(Map<String,String> params) throws InvalidParamsException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyGroup> resultCriteria = cb.createQuery(StudyGroup.class);
        Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
        resultCriteria.select(root);

        String filterParam = params.get("filter");
        String sortParam = params.get("sort");

        if (filterParam != null || sortParam != null) {
            Filter filter = new Filter(resultCriteria, cb, root);
            resultCriteria = (CriteriaQuery<StudyGroup>) filter.getResultCriteriaBuilder(filterParam, sortParam);
        }

        TypedQuery<StudyGroup> resultQuery = em.createQuery(resultCriteria);

        String pageNumberParam = params.get("pageNumber");
        if (pageNumberParam != null)
        {
            int pageNumber = Integer.parseInt(pageNumberParam);
            if (pageNumber < 0) throw new InvalidParamsException("Invalid Page Number");
            resultQuery.setFirstResult(pageNumber);
        }

        String pageSizeParam = params.get("pageSize");
        if (pageSizeParam != null)
        {
            int pageSize = Integer.parseInt(pageSizeParam);
            if (pageSize < 0) throw new InvalidParamsException("Invalid Page Size");

            resultQuery.setMaxResults(pageSize);
        } else {
            resultQuery.setMaxResults(10);
        }

        return resultQuery.getResultList();
    }

    @Override
    public StudyGroup findById(Long id) {
        Optional<StudyGroup> studyGroup = groupRepository.findById(id);
        return studyGroup.orElse(null);
    }

    @Override
    public StudyGroup save(StudyGroup entity) {
        return groupRepository.save(entity);
    }

    @Override
    public void delete(StudyGroup entity) {
        groupRepository.delete(entity);
    }

    @Override
    @Transactional
    public List<StudyGroup> getAllBySelectCriteria(String filterParam) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyGroup> resultCriteria = cb.createQuery(StudyGroup.class);
        Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
        resultCriteria.select(root);

        Filter filter = new Filter(resultCriteria, cb, root);
        resultCriteria = (CriteriaQuery<StudyGroup>) filter.getResultCriteriaBuilder(filterParam, null);

        TypedQuery<StudyGroup> resultQuery = em.createQuery(resultCriteria);

        return resultQuery.getResultList();
    }

    @Override
    @Transactional
    public Long getAllByCountCriteria(String filterParam) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> resultCriteria = cb.createQuery(Long.class);
        Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
        resultCriteria.select(cb.count(root));

        Filter filter = new Filter(resultCriteria, cb, root);
        resultCriteria = (CriteriaQuery<Long>) filter.getResultCriteriaBuilder(filterParam, null);

        TypedQuery<Long> resultQuery = em.createQuery(resultCriteria);

        return resultQuery.getSingleResult();
    }

    @Override
    @Transactional
    public int deleteAllByStudentCount(long studentCount) {

        return em.createQuery("delete from StudyGroup where studentsCount = :studentCount")
                .setParameter("studentCount", studentCount)
                .executeUpdate();
    }
}
