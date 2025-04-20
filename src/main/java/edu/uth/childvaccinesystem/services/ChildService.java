package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.ChildRepository;
import edu.uth.childvaccinesystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private UserRepository userRepository;  // Thêm UserRepository để truy vấn User

    public List<Child> getAllChildren() {
        return childRepository.findAll();
    }

    public Optional<Child> getChildById(Long id) {
        if (!childRepository.existsById(id)) {
            throw new RuntimeException("Child not found");
        }
        return childRepository.findById(id);
    }

    public List<Child> getChildrenByParentUsername(String parentUsername) {
        // Tìm User từ username
        Optional<User> parentOpt = userRepository.findByUsername(parentUsername);  // Sử dụng UserRepository để tìm User
        if (parentOpt.isPresent()) {
            User parent = parentOpt.get();
            List<Child> children = childRepository.findByParent(parent);  // Sử dụng mối quan hệ với User
            return children;
        }
        return List.of(); // Trả về danh sách trống nếu không tìm thấy User
    }

    public void saveChild(Child child) {
        // Nếu có parentUsername, tìm parent và set vào child
        if (child.getParentUsername() != null && !child.getParentUsername().isEmpty()) {
            Optional<User> parentOpt = userRepository.findByUsername(child.getParentUsername());
            if (parentOpt.isPresent()) {
                User parent = parentOpt.get();
                child.setParent(parent);
                // Ensure both relationships are set consistently
                if (child.getParentUsername() == null || !child.getParentUsername().equals(parent.getUsername())) {
                    child.setParentUsername(parent.getUsername());
                }
            }
        }
        childRepository.save(child);  // Lưu trẻ em vào database
    }

    public String updateChild(Long id, Child childDetails) {
        Optional<Child> optionalChild = childRepository.findById(id);
        if (optionalChild.isPresent()) {    
            Child child = optionalChild.get();
            
            // Update all editable fields
            child.setName(childDetails.getName());
            child.setDob(childDetails.getDob());
            child.setGender(childDetails.getGender());
            
            // Keep the relationship fields if they exist in childDetails
            if (childDetails.getParent() != null) {
                child.setParent(childDetails.getParent());
            }
            
            if (childDetails.getParentUsername() != null && !childDetails.getParentUsername().isEmpty()) {
                child.setParentUsername(childDetails.getParentUsername());
                
                // Update parent relationship if it doesn't exist
                if (child.getParent() == null) {
                    Optional<User> parentOpt = userRepository.findByUsername(childDetails.getParentUsername());
                    parentOpt.ifPresent(child::setParent);
                }
            }
            
            childRepository.save(child);
            return String.valueOf(child.getId());
        }
        throw new RuntimeException("Không tìm thấy hồ sơ trẻ");
    }

    public long deleteChild(Long id) {
        if (childRepository.existsById(id)) {
            childRepository.deleteById(id);
            return id;
        } else {
            throw new RuntimeException("Child not found");
        }
    }
}
