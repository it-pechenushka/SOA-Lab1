package app.controller;

import app.dto.StudyGroupsDto;
import app.exception.DataNotFoundException;
import app.exception.FilterParamException;
import app.exception.InvalidParamsException;
import app.helper.CommandType;
import app.helper.ResponseBuilder;
import app.helper.common.ErrorMessages;
import app.service.StudyGroupServiceImpl;
import lombok.SneakyThrows;
import app.model.StudyGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/special/groups", produces = "text/xml;charset=utf-8")
public class SpecialOperationController {
    private StudyGroupServiceImpl studyGroupService;

    @Autowired
    @SneakyThrows
    public SpecialOperationController(StudyGroupServiceImpl studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<?> getGroupsByCriteria(@RequestParam(name = "filter") String filterParam, @RequestParam(name = "commandType") String commandTypeParam) {
        try {
            CommandType commandType = CommandType.valueOf(commandTypeParam);

            if (commandType == CommandType.SELECT){
                StudyGroupsDto groupsDto = new StudyGroupsDto();
                List<StudyGroup> groups = studyGroupService.getAllBySelectCriteria(filterParam);

                if (groups.size() == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                groupsDto.setGroups(groups);

                return new ResponseEntity<>(groupsDto, HttpStatus.OK);

            } else {
                Long count = studyGroupService.getAllByCountCriteria(filterParam);

                if (count == 0)
                    throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

                return new ResponseEntity<>(ResponseBuilder.buildTextResponse(count + " entities found by this criteria!"), HttpStatus.OK);
            }

        } catch (NumberFormatException e){
            throw new FilterParamException(ErrorMessages.WARNING_FLOAT, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_PAGINATION);
        } catch (IllegalArgumentException e) {
            throw new InvalidParamsException("Command type - " + commandTypeParam + " is not supported");
        }
    }

    @SneakyThrows
    @DeleteMapping(value = "/{studentCount}")
    public ResponseEntity<?> deleteGroups(@PathVariable long studentCount) {
        int result = studyGroupService.deleteAllByStudentCount(studentCount);

        if (result < 1)
            throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

        return new ResponseEntity<>(ResponseBuilder.buildTextResponse("Entities successfully deleted!"), HttpStatus.OK);
    }
}
