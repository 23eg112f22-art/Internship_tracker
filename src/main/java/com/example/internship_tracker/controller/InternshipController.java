package com.example.internship_tracker.controller;

import com.example.internship_tracker.model.Internship;
import com.example.internship_tracker.model.Status;
import com.example.internship_tracker.model.User;
import com.example.internship_tracker.service.InternshipService;
import com.example.internship_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private UserService userService;

    private User getCurrentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName());
    }

    @GetMapping
    public List<Internship> getAllInternships(Authentication authentication) {
        return internshipService.getAllInternships(getCurrentUser(authentication));
    }

    @GetMapping("/{id}")
    public Internship getInternshipById(@PathVariable Long id, Authentication authentication) {
        return internshipService.getInternshipById(id, getCurrentUser(authentication));
    }

    @PostMapping
    public Internship createInternship(@RequestBody Internship internship, Authentication authentication) {
        return internshipService.createInternship(internship, getCurrentUser(authentication));
    }

    @PutMapping("/{id}")
    public Internship updateInternship(@PathVariable Long id, @RequestBody Internship internship, Authentication authentication) {
        return internshipService.updateInternship(id, internship, getCurrentUser(authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInternship(@PathVariable Long id, Authentication authentication) {
        internshipService.deleteInternship(id, getCurrentUser(authentication));
        return ResponseEntity.ok(Map.of("message", "Internship deleted successfully"));
    }

    @GetMapping("/search")
    public List<Internship> searchInternships(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        return internshipService.searchInternships(getCurrentUser(authentication), company, status, startDate, endDate);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return Map.of(
                "statusCounts", internshipService.getStatusCounts(user),
                "recentInternships", internshipService.getRecentInternships(user),
                "successRate", internshipService.getSuccessRate(user)
        );
    }
}
