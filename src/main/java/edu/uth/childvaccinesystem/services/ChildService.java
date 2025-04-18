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
        User parent = userRepository.findByUsername(parentUsername);  // Sử dụng UserRepository để tìm User
        if (parent != null) {
            List<Child> children = childRepository.findByParent(parent);  // Sử dụng mối quan hệ với User
            System.out.println("Danh sách trẻ em cho " + parentUsername + ": " + children.size()); // Debug log
            return children;
        }
        return List.of(); // Trả về danh sách trống nếu không tìm thấy User
    }

    public void saveChild(Child child) {
        childRepository.save(child);  // Kiểm tra lưu trẻ em vào database
    }

    public String updateChild(Long id, Child childDetails) {
        Optional<Child> optionalChild = childRepository.findById(id);
        if (optionalChild.isPresent()) {    
            Child child = optionalChild.get();
            child.setName(childDetails.getName());
            child.setDob(childDetails.getDob());
            childRepository.save(child);
            return String.valueOf(child.getId());
        }
        return "Child not found";
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
