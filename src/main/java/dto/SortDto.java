package dto;

import helper.processing.sort.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortDto {
    private String filedName;
    private SortType sortType;
}
