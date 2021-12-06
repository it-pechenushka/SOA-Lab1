package controller;

import database.DatabaseContext;
import exception.DataNotFoundException;
import exception.FilterParamException;
import exception.InvalidParamsException;
import exception.ValidationException;
import helper.CommonValidator;
import helper.processing.Filter;
import helper.ResponseBuilder;
import dto.StudyGroupsDto;
import helper.common.ErrorMessages;
import lombok.SneakyThrows;
import model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseOperationController extends HttpServlet {
    private SessionFactory sessionFactory;
    private Validator validator;
    private Unmarshaller unmarshaller;

    @SneakyThrows
    @Override
    public void init() {
        sessionFactory = DatabaseContext.getSessionFactory();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        unmarshaller = JAXBContext.newInstance(StudyGroup.class).createUnmarshaller();
        unmarshaller.setEventHandler(event -> false);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/xml");
        super.service(req, resp);
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        StringWriter responseContent = new StringWriter();

        if (req.getPathInfo() == null) {
            EntityManager em = sessionFactory.createEntityManager();
            em.getTransaction().begin();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StudyGroup> resultCriteria = cb.createQuery(StudyGroup.class);
            Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
            resultCriteria.select(root);

            try {
                if (req.getQueryString() != null) {
                    Filter filter = new Filter(req, resultCriteria, cb, root);
                    resultCriteria = (CriteriaQuery<StudyGroup>) filter.getResultCriteriaBuilder();
                }

                TypedQuery<StudyGroup> resultQuery = em.createQuery(resultCriteria);

                if (req.getParameter("pageNumber") != null)
                {
                    int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
                    if (pageNumber < 0) throw new InvalidParamsException("Invalid Page Number");
                    resultQuery.setFirstResult(pageNumber);
                }

                if (req.getParameter("pageSize") != null)
                {
                    int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                    if (pageSize < 0) throw new InvalidParamsException("Invalid Page Size");

                    resultQuery.setMaxResults(pageSize);
                } else {
                    resultQuery.setMaxResults(10);
                }

                List<StudyGroup> groups = resultQuery.getResultList();

                StudyGroupsDto groupsDto = new StudyGroupsDto();

                if (groups.size() == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                groupsDto.setGroups(groups);

                JAXB.marshal(groupsDto, responseContent);
                em.close();

            } catch (NumberFormatException e){
                throw new FilterParamException(ErrorMessages.WARNING_FLOAT, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_PAGINATION);
            }
        } else {

            long id = CommonValidator.validateId(req.getPathInfo());
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            StudyGroup studyGroup = session.get(StudyGroup.class, id);

            if (studyGroup == null){
                throw new DataNotFoundException(ErrorMessages.ENTITY_NOT_FOUND);
            } else {
                JAXB.marshal(studyGroup, responseContent);
            }

            session.close();
        }

        resp.getWriter().println(responseContent);

    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Session session = sessionFactory.openSession();

        StudyGroup studyGroup = deserializeStudyGroup(getRequestAsString(req));
        Set<ConstraintViolation<StudyGroup>> violations = validator.validate(studyGroup);
        if (violations.size() > 0)
            throw new ValidationException(violations);

        session.beginTransaction();

        session.save(studyGroup);

        session.getTransaction().commit();

        resp.getWriter().println(ResponseBuilder.buildTextResponse("'Study Group' successfully created!"));

        session.close();
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Session session = sessionFactory.openSession();

        StudyGroup studyGroupUpdate = deserializeStudyGroup(getRequestAsString(req));

        if (studyGroupUpdate.getId() == null)
            throw new InvalidParamsException(ErrorMessages.INVALID_ID);

        session.beginTransaction();
        StudyGroup studyGroup = session.get(StudyGroup.class, studyGroupUpdate.getId());

        if (studyGroup == null)
            throw new DataNotFoundException(ErrorMessages.ENTITY_NOT_FOUND);

        toUpdate(studyGroup, studyGroupUpdate);
        Set<ConstraintViolation<StudyGroup>> violations = validator.validate(studyGroup);

        if (violations.size() > 0)
            throw new ValidationException(violations);

        session.merge(studyGroup);
        session.getTransaction().commit();

        resp.getWriter().println(ResponseBuilder.buildTextResponse("'Study Group' successfully updated!"));

        session.close();
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Session session = sessionFactory.openSession();

        long id = CommonValidator.validateId(req.getPathInfo());
        session.beginTransaction();

        StudyGroup studyGroup = session.get(StudyGroup.class, id);;

        if (studyGroup == null)
            throw new DataNotFoundException(ErrorMessages.ENTITY_NOT_FOUND);

        session.delete(studyGroup);

        session.getTransaction().commit();

        resp.getWriter().println(ResponseBuilder.buildTextResponse("'Study Group' successfully deleted!"));

        session.clear();
        session.close();
    }

    @Override
    public void destroy() {
        sessionFactory.close();
        super.destroy();
        System.out.println("Controller has been destroyed");
    }

    private String getRequestAsString(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private StudyGroup deserializeStudyGroup(String requestString) throws JAXBException {
        return (StudyGroup) unmarshaller.unmarshal(new StringReader(requestString));
    }

    private void toUpdate(StudyGroup old, StudyGroup update) {
        if (update.getName() != null && !update.getName().isEmpty())
            old.setName(update.getName());

        if (update.getXCoordinate() != null)
            old.setXCoordinate(update.getXCoordinate());

        if (update.getYCoordinate() != null)
            old.setYCoordinate(update.getYCoordinate());

        if (update.getFormOfEducation() != null)
            old.setFormOfEducation(update.getFormOfEducation());

        if (update.getExpelledStudents() != null)
            old.setExpelledStudents(update.getExpelledStudents());

        if (update.getShouldBeExpelled() != null)
            old.setShouldBeExpelled(update.getShouldBeExpelled());

        if (update.getStudentsCount() != null)
            old.setStudentsCount(update.getStudentsCount());

        if (update.getGroupAdmin() != null){
            Person adminUpdate = update.getGroupAdmin();
            Person adminOld = old.getGroupAdmin() == null ? new Person() : old.getGroupAdmin();

            if (adminUpdate.getName() != null && !adminUpdate.getName().isEmpty())
                adminOld.setName(adminUpdate.getName());

            if (adminUpdate.getNationality() != null)
                adminOld.setNationality(adminUpdate.getNationality());

            if (adminUpdate.getWeight() != null)
                adminOld.setWeight(adminUpdate.getWeight());

            if (adminUpdate.getHairColor() != null)
                adminOld.setHairColor(adminUpdate.getHairColor());

            if (adminUpdate.getLocation() != null){
                Location locationUpdate = adminUpdate.getLocation();
                Location locationOld = adminOld.getLocation() == null ? new Location() : adminOld.getLocation();

                if (locationUpdate.getXLocation() != null)
                    locationOld.setXLocation(locationUpdate.getXLocation());

                if (locationUpdate.getYLocation() != null)
                    locationOld.setYLocation(locationUpdate.getYLocation());

                if (locationUpdate.getZLocation() != null)
                    locationOld.setZLocation(locationUpdate.getZLocation());

                adminOld.setLocation(locationOld);
            }

            old.setGroupAdmin(adminOld);
        }
    }

}
