package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/child")
public class ChildController {

    @Autowired
    private ChildService childService;

    @GetMapping
    public List<Child> getAllChildren() {
        return childService.getAllChildren();
    }

    @PostMapping
    public ResponseEntity<Long> createChild(@RequestBody Child child) {
        long id = Long.parseLong(childService.saveChild(child));
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChild(@PathVariable Long id, @RequestBody Child childDetails) {
        try {
            long updatedId = Long.parseLong(childService.updateChild(id, childDetails));
            return ResponseEntity.ok(updatedId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteChild(@PathVariable Long id) {
        try {
            long deletedId = childService.deleteChild(id);
            return ResponseEntity.ok(deletedId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
