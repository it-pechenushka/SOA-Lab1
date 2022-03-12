package app.dto;

import app.helper.processing.filter.FilterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
    private String filedName;
    private String value;
    private FilterType filterType;
}
