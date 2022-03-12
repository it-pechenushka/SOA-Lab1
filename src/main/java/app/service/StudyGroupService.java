package app.service;

import app.exception.InvalidParamsException;
import app.model.StudyGroup;

import java.util.List;
import java.util.Map;

public interface StudyGroupService {
    List<StudyGroup> findAll(Map<String,String> params) throws InvalidParamsException;
    StudyGroup findById(Long id);

    StudyGroup save(StudyGroup entity);
    void delete(StudyGroup entity);
    List<StudyGroup> getAllBySelectCriteria(String filterParam);
    Long getAllByCountCriteria(String filterParam);
    int deleteAllByStudentCount(long studentCount);
}
