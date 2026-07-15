package cloudflight.integra.backend.tag.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "tags",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)
public class Tag {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    public Tag() {}

    public Tag(Long id, String name, Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public Tag setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Tag setName(String name) {
        this.name = name;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Tag setCategory(Category category) {
        this.category = category;
        return this;
    }

    @Override
    public String toString() {
        return "Tag #" + this.id + "\t[" + this.category + "]:\t\"" + this.name + "\"";
    }
}
