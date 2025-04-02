package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.repositories.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    public List<Child> getAllChildren() {
        return childRepository.findAll();
    }

    public Optional<Child> getChildById(Long id) {
        if (!childRepository.existsById(id)) {
            throw new RuntimeException("Child not found");
        }
        return childRepository.findById(id);
    }

    public String saveChild(Child child) {
        // Check if a child with the same name and date of birth already exists
        if (childRepository.existsByNameAndDob(child.getName(), child.getDob())) {
            return "Child already exists with the same name and date of birth";
        }
        childRepository.save(child).getId();
        return "Child saved successfully";
    }

    public String updateChild(Long id, Child childDetails) {
        Optional<Child> optionalChild = childRepository.findById(id);
        if (optionalChild.isPresent()) {
            Child child = optionalChild.get();
            child.setName(childDetails.getName());
            child.setDob(childDetails.getDob());
            childRepository.save(child).getId();
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