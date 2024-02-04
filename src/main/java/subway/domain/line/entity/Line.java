package subway.domain.line.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.common.exception.CustomException;
import subway.domain.station.entity.Station;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Getter
    @OneToMany(mappedBy = "line")
    private List<Section> sections;

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections = new ArrayList<>();
        this.sections.add(section);
        section.setLine(this);
    }

    public Station getUpStation() {
        return sections.get(0).getUpStation();
    }

    public Station getDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public void changeName(String name) {
        if (name == null) {
            return;
        }
        if (name.isEmpty()) {
            return;
        }
        if (name.isBlank()) {
            return;
        }
        this.name = name;
    }

    public void changeColor(String color) {
        if (color == null) {
            return;
        }
        if (color.isEmpty()) {
            return;
        }
        if (color.isBlank()) {
            return;
        }
        this.color = color;
    }

    public void add(Section section) {
        if (!this.getDownStation().equals(section.getUpStation())) {
            throw new InvalidParameterException("잘못된 상행역");
        }

        if (this.contains(section.getDownStation())) {
            throw new CustomException.Conflict("이미 포함된 하행역");
        }
        this.sections.add(section);
        section.setLine(this);
    }


    public void remove(Long sectionId) {
        Section lastSection = this.sections.get(this.sections.size()-1);

        if (!lastSection.getDownStation().getId().equals(sectionId)) {
            throw new InvalidParameterException("잘못된 하행역");
        }

        if(this.sections.size() == 1) {
            throw new InvalidParameterException("유일한 구간");
        }

        lastSection.setLine(null);
        this.sections.remove(lastSection);
    }

    public boolean contains(Station station) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(station));
    }

}
