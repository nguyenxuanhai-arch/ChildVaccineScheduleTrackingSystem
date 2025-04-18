package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender")
    private String gender; // VD: "MALE" hoặc "FEMALE"

    @ManyToOne
    @JoinColumn(name = "parent_id") // Thêm trường khóa ngoại để liên kết với User
    private User parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    // Getter/Setter đã có từ @Getter @Setter, nhưng nếu bạn cần override hoặc bổ sung logic, có thể ghi rõ như dưới:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public User getParent() { return parent; }
    public void setParent(User parent) { this.parent = parent; }
}
