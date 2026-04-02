package com.example.internship_tracker.service;

import com.example.internship_tracker.model.Internship;
import com.example.internship_tracker.model.Status;
import com.example.internship_tracker.model.User;
import com.example.internship_tracker.repository.InternshipRepository;
import com.example.internship_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    public List<Internship> getAllInternships(User user) {
        return internshipRepository.findByUser(user);
    }

    public Internship getInternshipById(Long id, User user) {
        Internship internship = internshipRepository.findById(id).orElseThrow(() -> new RuntimeException("Internship not found"));
        if (!internship.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized");
        }
        return internship;
    }

    public Internship createInternship(Internship internship, User user) {
        internship.setUser(user);
        return internshipRepository.save(internship);
    }

    public Internship updateInternship(Long id, Internship updatedInternship, User user) {
        Internship existing = getInternshipById(id, user);
        existing.setCompanyName(updatedInternship.getCompanyName());
        existing.setRole(updatedInternship.getRole());
        existing.setApplicationDate(updatedInternship.getApplicationDate());
        existing.setStatus(updatedInternship.getStatus());
        existing.setDeadline(updatedInternship.getDeadline());
        existing.setNotes(updatedInternship.getNotes());
        return internshipRepository.save(existing);
    }

    public void deleteInternship(Long id, User user) {
        Internship internship = getInternshipById(id, user);
        internshipRepository.delete(internship);
    }

    public List<Internship> searchInternships(User user, String company, Status status, LocalDate startDate, LocalDate endDate) {
        List<Internship> internships = internshipRepository.findByUser(user);
        if (company != null && !company.isEmpty()) {
            internships = internships.stream()
                    .filter(i -> i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (status != null) {
            internships = internships.stream()
                    .filter(i -> i.getStatus().equals(status))
                    .collect(Collectors.toList());
        }
        if (startDate != null && endDate != null) {
            internships = internships.stream()
                    .filter(i -> !i.getApplicationDate().isBefore(startDate) && !i.getApplicationDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        return internships;
    }

    public List<Internship> getRecentInternships(User user) {
        return internshipRepository.findByUserOrderByApplicationDateDesc(user).stream().limit(10).collect(Collectors.toList());
    }

    public Map<Status, Long> getStatusCounts(User user) {
        return Map.of(
                Status.APPLIED, internshipRepository.countByUserAndStatus(user, Status.APPLIED),
                Status.INTERVIEW, internshipRepository.countByUserAndStatus(user, Status.INTERVIEW),
                Status.REJECTED, internshipRepository.countByUserAndStatus(user, Status.REJECTED),
                Status.OFFER, internshipRepository.countByUserAndStatus(user, Status.OFFER)
        );
    }

    public double getSuccessRate(User user) {
        long total = internshipRepository.findByUser(user).size();
        long offers = internshipRepository.countByUserAndStatus(user, Status.OFFER);
        return total == 0 ? 0 : (double) offers / total * 100;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void sendDeadlineReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Internship> upcoming = internshipRepository.findUpcomingDeadlines(user, tomorrow, tomorrow);
            for (Internship i : upcoming) {
                sendEmail(user.getEmail(), "Deadline Reminder", "Your application for " + i.getRole() + " at " + i.getCompanyName() + " is due tomorrow.");
            }
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
