package edu.uth.childvaccinesystem.services.impl;

import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.mappers.ChildMapper;
import edu.uth.childvaccinesystem.repositories.ChildRepository;
import edu.uth.childvaccinesystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class ChildService {
    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final ChildMapper childMapper;

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
        Optional<User> parentOpt = userRepository.findByUsername(parentUsername);  // Sử dụng UserRepository để tìm User
        
        List<Child> childrenList = new ArrayList<>();
        
        if (parentOpt.isPresent()) {
            User parent = parentOpt.get();
            List<Child> childrenByParent = childRepository.findByParent(parent);  // Sử dụng mối quan hệ với User
            childrenList.addAll(childrenByParent);
            
            System.out.println("Found " + childrenByParent.size() + " children by parent object");
        }
        
        List<Child> childrenByUsername = childRepository.findByParentUsername(parentUsername);
        
        System.out.println("Found " + childrenByUsername.size() + " children by parentUsername");
        
        for (Child child : childrenByUsername) {
            if (!childrenList.contains(child)) {
                childrenList.add(child);
            }
        }
        
        System.out.println("Total children found for parent " + parentUsername + ": " + childrenList.size());
        
        return childrenList;
    }

    public Child saveChild(Child child) {
        System.out.println("Saving child in service:");
        System.out.println("Name: " + child.getName());
        System.out.println("DOB: " + child.getDob());
        System.out.println("Gender: " + child.getGender());
        System.out.println("ParentUsername: " + child.getParentUsername());

        if (child.getParentUsername() != null && !child.getParentUsername().isEmpty()) {
            Optional<User> parentOpt = userRepository.findByUsername(child.getParentUsername());
            if (parentOpt.isPresent()) {
                User parent = parentOpt.get();
                child.setParent(parent);
                if (child.getParentUsername() == null || !child.getParentUsername().equals(parent.getUsername())) {
                    child.setParentUsername(parent.getUsername());
                }
            }
        }

        Child savedChild = childRepository.save(child);
        
        System.out.println("Saved child in service:");
        System.out.println("ID: " + savedChild.getId());
        System.out.println("Name: " + savedChild.getName());
        System.out.println("DOB: " + savedChild.getDob());
        System.out.println("Gender: " + savedChild.getGender());
        System.out.println("ParentUsername: " + savedChild.getParentUsername());

        return savedChild;
    }

    public String updateChild(Long id, Child childDetails) {
        Optional<Child> optionalChild = childRepository.findById(id);
        if (optionalChild.isPresent()) {    
            Child child = optionalChild.get();
            
            child.setName(childDetails.getName());
            child.setDob(childDetails.getDob());
            child.setGender(childDetails.getGender());
            
            if (childDetails.getParent() != null) {
                child.setParent(childDetails.getParent());
            }
            
            if (childDetails.getParentUsername() != null && !childDetails.getParentUsername().isEmpty()) {
                child.setParentUsername(childDetails.getParentUsername());
                
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
