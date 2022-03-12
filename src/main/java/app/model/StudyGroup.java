package app.model;

import app.helper.LocalDateAdapter;
import app.helper.validation.GreaterThan;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "study_group")
@Getter
@Setter
@XmlRootElement(name = "StudyGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudyGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotNull(message = "должно иметь значение")
    @NotEmpty
    @Column(name = "name", nullable = false)
    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z_0-9]*", message = "должно начинаться с латинской буквы или сивола подчеркивания и не содержать специальных символов")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @GreaterThan(-406)
    @Column(name = "x_coordinate", nullable = false)
    private Float xCoordinate; //Значение поля должно быть больше -406, Поле не может быть null

    @GreaterThan(-345)
    @Column(name = "y_coordinate", nullable = false)
    private Float yCoordinate;

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    @NotNull(message = "должно иметь значение")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Min(1)
    @NotNull(message = "должно иметь значение")
    @Column(name = "students_count", nullable = false)
    private Long studentsCount; //Значение поля должно быть больше 0, Поле не может быть null

    @Min(1)
    @NotNull(message = "должно иметь значение")
    @Column(name = "expelled_students")
    private Long expelledStudents; //Значение поля должно быть больше 0

    @Min(1)
    @NotNull(message = "должно иметь значение")
    @Column(name = "should_be_expelled", nullable = false)
    private Integer shouldBeExpelled; //Значение поля должно быть больше 0, Поле не может быть null

    @NotNull(message = "должно иметь значение")
    @Column(name = "form_of_education", nullable = false)
    @Enumerated(EnumType.STRING)
    private FormOfEducation formOfEducation; //Поле не может быть null

    @Valid
    @XmlElement(name = "GroupAdmin")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_admin_id", referencedColumnName = "id")
    private Person groupAdmin; //Поле может быть null

    public StudyGroup(){
        creationDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return "StudyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                ", creationDate=" + creationDate +
                ", studentsCount=" + studentsCount +
                ", expelledStudents=" + expelledStudents +
                ", shouldBeExpelled=" + shouldBeExpelled +
                ", formOfEducation=" + formOfEducation +
                ", groupAdmin=" + groupAdmin +
                '}';
    }
}
