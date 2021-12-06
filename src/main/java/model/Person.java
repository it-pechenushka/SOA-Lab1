package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@Entity
@Table(name = "group_admin")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@Getter
@Setter
public class Person implements Serializable, Comparable<Person> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "должно иметь значение")
    @NotEmpty
    @Column(name = "name")
    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z_0-9]*", message = "должно начинаться с латинской буквы или сивола подчеркивания и не содержать специальных символов")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Min(1)
    @NotNull(message = "должно иметь значение")
    @Column(name = "weight", nullable = false)
    private Integer weight; //Значение поля должно быть больше 0

    @NotNull(message = "должно иметь значение")
    @Column(name = "nationality", nullable = false)
    private Country nationality; //Поле не может быть null

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @XmlElement(name = "location")
    private Location location; //Поле может быть null

    @Column(name = "hair_color")
    private Color hairColor; //Поле может быть null

    @XmlTransient
    @OneToOne(mappedBy = "groupAdmin")
    private StudyGroup studyGroup;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", nationality=" + nationality +
                ", location=" + location +
                ", hairColor=" + hairColor +
                '}';
    }

    @Override
    public int compareTo(Person o) {
        return this.weight - o.getWeight();
    }
}
