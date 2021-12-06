package controller;

import database.DatabaseContext;
import dto.StudyGroupsDto;
import exception.DataNotFoundException;
import exception.FilterParamException;
import exception.InvalidParamsException;
import helper.CommandType;
import helper.CommonValidator;
import helper.ResponseBuilder;
import helper.common.ErrorMessages;
import helper.processing.Filter;
import lombok.SneakyThrows;
import model.StudyGroup;
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
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class SpecialOperationController extends HttpServlet {
    private SessionFactory sessionFactory;

    @SneakyThrows
    @Override
    public void init() {
        sessionFactory = DatabaseContext.getSessionFactory();
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
        if (req.getPathInfo() != null || req.getParameter("commandType") == null)
            throw new InvalidParamsException("Invalid path params or command type does not presented!");

        if (req.getParameter("filter") == null)
            throw new InvalidParamsException("No one filter param set!");

        try {
            CommandType commandType = CommandType.valueOf(req.getParameter("commandType"));
            EntityManager em = sessionFactory.createEntityManager();
            em.getTransaction().begin();

            CriteriaBuilder cb = em.getCriteriaBuilder();

            if (commandType == CommandType.SELECT){
                CriteriaQuery<StudyGroup> resultCriteria = cb.createQuery(StudyGroup.class);
                Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
                resultCriteria.select(root);

                Filter filter = new Filter(req, resultCriteria, cb, root);
                resultCriteria = (CriteriaQuery<StudyGroup>) filter.getResultCriteriaBuilder();

                TypedQuery<StudyGroup> resultQuery = em.createQuery(resultCriteria);

                List<StudyGroup> groups = resultQuery.getResultList();

                StudyGroupsDto groupsDto = new StudyGroupsDto();

                if (groups.size() == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                groupsDto.setGroups(groups);

                JAXB.marshal(groupsDto, responseContent);

            } else {
                CriteriaQuery<Long> resultCriteria = cb.createQuery(Long.class);
                Root<StudyGroup> root = resultCriteria.from(StudyGroup.class);
                resultCriteria.select(cb.count(root));

                Filter filter = new Filter(req, resultCriteria, cb, root);
                resultCriteria = (CriteriaQuery<Long>) filter.getResultCriteriaBuilder();

                TypedQuery<Long> resultQuery = em.createQuery(resultCriteria);

                Long count = resultQuery.getSingleResult();

                if (count == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                responseContent.write(ResponseBuilder.buildTextResponse(count + " entities found by this criteria!"));
            }

        } catch (NumberFormatException e){
            throw new FilterParamException(ErrorMessages.WARNING_FLOAT, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_PAGINATION);
        }


        resp.getWriter().println(responseContent);
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Session session = sessionFactory.openSession();

        long studentCount = CommonValidator.validateId(req.getPathInfo());
        session.beginTransaction();
        int result = session.createQuery("delete from StudyGroup where studentsCount = :studentCount").setParameter("studentCount", studentCount).executeUpdate();

        if (result < 1)
            throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

        resp.getWriter().println(ResponseBuilder.buildTextResponse("Entities successfully deleted!"));
        session.close();
    }

    @Override
    public void destroy() {
        sessionFactory.close();
        super.destroy();
        System.out.println("Controller has been destroyed");
    }

}
