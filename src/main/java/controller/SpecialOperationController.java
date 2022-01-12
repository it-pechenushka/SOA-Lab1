package controller;

import dto.StudyGroupsDto;
import exception.DataNotFoundException;
import exception.FilterParamException;
import exception.InvalidParamsException;
import helper.CommandType;
import helper.ResponseBuilder;
import helper.common.ErrorMessages;
import helper.processing.Filter;
import lombok.SneakyThrows;
import model.StudyGroup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@RestController
@RequestMapping(value = "/special/groups", produces = "text/xml;charset=utf-8")
public class SpecialOperationController {
    private final SessionFactory sessionFactory;

    @Autowired
    @SneakyThrows
    public SpecialOperationController(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<?> getGroupsByCriteria(@RequestParam(name = "filter") String filterParam, @RequestParam(name = "commandType") String commandTypeParam) {
        StudyGroupsDto groupsDto = new StudyGroupsDto();

        try {
            CommandType commandType = CommandType.valueOf(commandTypeParam);
            EntityManager em = sessionFactory.createEntityManager();
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            if (commandType == CommandType.SELECT){
                CriteriaQuery<StudyGroup> resultCriteria = cb.createQuery(StudyGroup.class);
                Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
                resultCriteria.select(root);

                Filter filter = new Filter(resultCriteria, cb, root);
                resultCriteria = (CriteriaQuery<StudyGroup>) filter.getResultCriteriaBuilder(filterParam, null);

                TypedQuery<StudyGroup> resultQuery = em.createQuery(resultCriteria);

                List<StudyGroup> groups = resultQuery.getResultList();

                if (groups.size() == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                groupsDto.setGroups(groups);

            } else {
                CriteriaQuery<Long> resultCriteria = cb.createQuery(Long.class);
                Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
                resultCriteria.select(cb.count(root));

                Filter filter = new Filter(resultCriteria, cb, root);
                resultCriteria = (CriteriaQuery<Long>) filter.getResultCriteriaBuilder(filterParam, null);

                TypedQuery<Long> resultQuery = em.createQuery(resultCriteria);

                Long count = resultQuery.getSingleResult();

                if (count == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                return new ResponseEntity<>(ResponseBuilder.buildTextResponse(count + " entities found by this criteria!"), HttpStatus.OK);
            }

        } catch (NumberFormatException e){
            throw new FilterParamException(ErrorMessages.WARNING_FLOAT, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_PAGINATION);
        } catch (IllegalArgumentException e) {
            throw new InvalidParamsException("Command type - " + commandTypeParam + " is not supported");
        }

        return new ResponseEntity<>(groupsDto, HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping(value = "/{studentCount}")
    public ResponseEntity<?> deleteGroups(@PathVariable long studentCount) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();
        int result = session
                .createQuery("delete from StudyGroup where studentsCount = :studentCount")
                .setParameter("studentCount", studentCount)
                .executeUpdate();

        if (result < 1)
            throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

        session.close();

        return new ResponseEntity<>(ResponseBuilder.buildTextResponse("Entities successfully deleted!"), HttpStatus.OK);
    }

    @PreDestroy
    public void preDestroy() {
        sessionFactory.close();
        System.out.println("Beans has been pre-destroyed");
    }

}
