package app.controller;

import app.dto.StudyGroupsDto;
import app.exception.DataNotFoundException;
import app.exception.FilterParamException;
import app.exception.InvalidParamsException;
import app.exception.ValidationException;
import app.helper.ResponseBuilder;
import app.helper.common.ErrorMessages;
import app.model.Location;
import app.model.Person;
import app.model.StudyGroup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.service.StudyGroupServiceImpl;

import javax.validation.*;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/base/groups", produces = "text/xml;charset=utf-8")
public class BaseOperationController {
    private StudyGroupServiceImpl studyGroupService;
    private final Validator validator;
    private final Unmarshaller unmarshaller;

    @Autowired
    @SneakyThrows
    public BaseOperationController(StudyGroupServiceImpl studyGroupService, Unmarshaller unmarshaller, Validator validator) {
        this.validator = validator;
        this.unmarshaller = unmarshaller;
        this.studyGroupService = studyGroupService;
    }

    @SneakyThrows
    @GetMapping(value = "/{id}")
    public ResponseEntity<StudyGroup> getSingleGroup(@PathVariable long id) {
        if (id < 0)
            throw new InvalidParamsException(ErrorMessages.INVALID_ID);

            StudyGroup studyGroup = studyGroupService.findById(id);

            if (studyGroup == null)
                throw new DataNotFoundException(ErrorMessages.ENTITY_NOT_FOUND);

            return new ResponseEntity<>(studyGroup, HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<StudyGroupsDto> getGroups(@RequestParam Map<String,String> params) {
        for (String param: params.keySet()) {
            switch (param){
                case "sort":
                case "filter":
                case "pageSize":
                case "pageNumber":
                case "commandType":
                    continue;
                default:
                    throw new InvalidParamsException("Unexpected param name: " + param);
            }
        }

        StudyGroupsDto groupsDto = new StudyGroupsDto();
        try{

            List<StudyGroup> groups = studyGroupService.findAll(params);

            if (groups.size() == 0)
                throw new DataNotFoundException(ErrorMessages.EMPTY_ENTITY_LIST);

            groupsDto.setGroups(groups);

        } catch (NumberFormatException e){
            throw new FilterParamException(ErrorMessages.WARNING_FLOAT, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_PAGINATION);
        }

        return new ResponseEntity<>(groupsDto, HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping(produces = {MediaType.TEXT_XML_VALUE})
    public ResponseEntity<String> createGroup(@RequestBody String body) {
        StudyGroup studyGroup = deserializeStudyGroup(body);
        Set<ConstraintViolation<StudyGroup>> violations = validator.validate(studyGroup);
        if (violations.size() > 0)
            throw new ValidationException(violations);

        studyGroupService.save(studyGroup);

        return new ResponseEntity<>(ResponseBuilder.buildTextResponse("'Study Group' successfully created!"), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudyGroup(@PathVariable long id, @RequestBody String body) {
        StudyGroup studyGroupUpdate = deserializeStudyGroup(body);

        StudyGroup studyGroup = studyGroupService.findById(id);

        if (studyGroup == null)
            throw new DataNotFoundException(ErrorMessages.ENTITY_NOT_FOUND);

        toUpdate(studyGroup, studyGroupUpdate);
        Set<ConstraintViolation<StudyGroup>> violations = validator.validate(studyGroup);

        if (violations.size() > 0)
            throw new ValidationException(violations);

        studyGroupService.save(studyGroup);

        return new ResponseEntity<>(ResponseBuilder.buildTextResponse("'Study Group' successfully updated!"), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudyGroup(@PathVariable long id) {

        StudyGroup studyGroup = studyGroupService.findById(id);

        if (studyGroup == null)
            throw new DataNotFoundException(ErrorMessages.ENTITY_NOT_FOUND);

        studyGroupService.delete(studyGroup);

        return new ResponseEntity<>(ResponseBuilder.buildTextResponse("'Study Group' successfully deleted!"), HttpStatus.OK);
    }

    @SneakyThrows
    private StudyGroup deserializeStudyGroup(String requestBody) {
        return (StudyGroup) unmarshaller.unmarshal(new StringReader(requestBody));
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
