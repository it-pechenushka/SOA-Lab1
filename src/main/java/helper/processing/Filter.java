package helper.processing;

import com.google.gson.Gson;
import dto.FilterDto;
import dto.SortDto;
import exception.InvalidParamsException;
import helper.processing.filter.FilterPredicate;
import helper.processing.filter.FilterType;
import helper.processing.sort.SortPredicate;
import lombok.*;
import model.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Filter {
    private List<Order> orderList;
    private List<Predicate> predicateList;
    private CriteriaQuery<?> criteriaQuery;
    private Root<StudyGroup> root;
    private Path<Person> groupAdminPath;
    private Path<Location> locationPath;
    private FilterDto filterDto;
    private CriteriaBuilder criteriaBuilder;
    private FilterType filterType;

    public Filter(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<StudyGroup> root){
        this.criteriaQuery = criteriaQuery;
        this.root = root;
        this.groupAdminPath = root.get("groupAdmin");
        this.locationPath = groupAdminPath.get("location");
        this.criteriaBuilder = criteriaBuilder;
        this.orderList = new ArrayList<>();
        this.predicateList = new ArrayList<>();
    }

    public CriteriaQuery<?> getResultCriteriaBuilder(String filterParam, String sortParam) {
        Gson gsonParser = new Gson();

        SortDto[] sortDtos = gsonParser.fromJson(sortParam, SortDto[].class);
        if (sortDtos != null)
            for (SortDto sort: sortDtos)
                handle(sort.getFiledName(), sort);

        FilterDto[] filterDtos = gsonParser.fromJson(filterParam, FilterDto[].class);
        if (filterDtos != null)
            for (FilterDto filter: filterDtos)
                handle(filter.getFiledName(), filter);

        return criteriaQuery.where(predicateList.toArray(new Predicate[]{})).orderBy(orderList);
    }


    @SneakyThrows
    private void handle(String fieldName, Object data){
        Predicate filterPredicate = null;

        switch (fieldName) {
            case "name":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), filterData.getValue())
                            .getQueryFilterPredicate(criteriaBuilder, root);
                }
                break;
            case "yCoordinate":
            case "xCoordinate":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Float.parseFloat(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, root);
                }
                break;
            case "studentsCount":
            case "expelledStudents":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Long.parseLong(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, root);
                }
                break;
            case "shouldBeExpelled":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Integer.parseInt(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, root);
                }
                break;
            case "formOfEducation":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), FormOfEducation.valueOf(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, root);
                }
                break;
            case "groupAdmin":
                if(data.getClass() == SortDto.class){
                    SortDto sortData = (SortDto) data;
                    orderList.add(new SortPredicate("weight", sortData.getSortType()).getSortOrder(criteriaBuilder, groupAdminPath));
                }
                break;
            case "adminName":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>("name", filterData.getFilterType(), filterData.getValue())
                            .getQueryFilterPredicate(criteriaBuilder, groupAdminPath);
                }
                break;
            case "weight":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Integer.parseInt(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, groupAdminPath);
                }
                break;
            case "nationality":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Country.valueOf(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, groupAdminPath);
                }
                break;
            case "hairColor":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Color.valueOf(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, groupAdminPath);
                }
                break;
            case "xLocation":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Long.parseLong(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, locationPath);
                }
                break;
            case "yLocation":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Float.parseFloat(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, locationPath);
                }
                break;
            case "zLocation":
                if (data.getClass() == FilterDto.class)  {
                    FilterDto filterData = (FilterDto) data;
                    filterPredicate = new FilterPredicate<>(filterData.getFiledName(), filterData.getFilterType(), Integer.parseInt(filterData.getValue()))
                            .getQueryFilterPredicate(criteriaBuilder, locationPath);
                }
                break;
            default:
                throw new InvalidParamsException("Unexpected field name: " + fieldName);
        }

        if(data.getClass() == SortDto.class)
            addSortOrder((SortDto) data);

        if (filterPredicate != null)
            predicateList.add(filterPredicate);
    }

    private void addSortOrder(SortDto sortData){
        orderList.add(new SortPredicate(sortData.getFiledName(), sortData.getSortType()).getSortOrder(criteriaBuilder, root));
    }
}

