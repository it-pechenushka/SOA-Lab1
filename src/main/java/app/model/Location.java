package app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "x_location")
    private Long xLocation;

    @NotNull(message = "должно иметь значение")
    @Column(name = "y_location")
    private Float yLocation; //Поле не может быть null

    @NotNull(message = "должно иметь значение")
    @Column(name = "z_location")
    private Integer zLocation; //Поле не может быть null

    @XmlTransient
    @OneToOne(mappedBy = "location")
    private Person person;

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", xLocation=" + xLocation +
                ", yLocation=" + yLocation +
                ", zLocation=" + zLocation +
                '}';
    }
}
