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
    
    @Column(name = "parent_username")
    private String parentUsername;
    
    @Column(name = "place_of_birth")
    private String placeOfBirth;
    
    @Column(name = "nationality")
    private String nationality;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "blood_type")
    private String bloodType;
    
    @Column(name = "health_insurance_number")
    private String healthInsuranceNumber;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "parent_id") // Thêm trường khóa ngoại để liên kết với User
    private User parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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
    
    public String getParentUsername() { return parentUsername; }
    public void setParentUsername(String parentUsername) { this.parentUsername = parentUsername; }
}
