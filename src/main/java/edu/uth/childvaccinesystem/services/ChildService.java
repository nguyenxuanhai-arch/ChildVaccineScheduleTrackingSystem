package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.models.Child;
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
        return childRepository.findById(id);
    }

    public long saveChild(Child child) {
        return childRepository.save(child).getId();
    }

    public long updateChild(Long id, Child childDetails) {
        Optional<Child> optionalChild = childRepository.findById(id);
        if (optionalChild.isPresent()) {
            Child child = optionalChild.get();
            child.setName(childDetails.getName());
            child.setDob(childDetails.getDob());
            return childRepository.save(child).getId();
        } else {
            throw new RuntimeException("Child not found");
        }
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