package com.example.internship_tracker.repository;

import com.example.internship_tracker.model.Internship;
import com.example.internship_tracker.model.Status;
import com.example.internship_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByUser(User user);
    List<Internship> findByUserAndStatus(User user, Status status);
    List<Internship> findByUserAndCompanyNameContainingIgnoreCase(User user, String companyName);
    List<Internship> findByUserAndApplicationDateBetween(User user, LocalDate start, LocalDate end);
    List<Internship> findByUserOrderByDeadlineAsc(User user);
    List<Internship> findByUserOrderByApplicationDateDesc(User user);

    @Query("SELECT i FROM Internship i WHERE i.user = :user AND i.deadline BETWEEN :start AND :end")
    List<Internship> findUpcomingDeadlines(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(i) FROM Internship i WHERE i.user = :user AND i.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Status status);
}
