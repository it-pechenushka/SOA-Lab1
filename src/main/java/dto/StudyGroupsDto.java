package dto;

import lombok.Getter;
import lombok.Setter;
import model.StudyGroup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "Groups")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudyGroupsDto implements Serializable {

    @XmlElement(name = "StudyGroup")
    private List<StudyGroup> groups;
}
