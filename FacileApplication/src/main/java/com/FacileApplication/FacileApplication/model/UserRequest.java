package com.FacileApplication.FacileApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class - maps directly to the "user_requests" table in MySQL.
 * Hibernate will auto-create this table on startup (ddl-auto=update).
 */
@Entity
@Table(name = "fms_user_master")
@Data                   // Lombok: generates getters, setters, toString, equals, hashCode
@Builder                // Lombok: enables builder pattern
@NoArgsConstructor      // Lombok: generates no-arg constructor (required by JPA)
@AllArgsConstructor     // Lombok: generates all-args constructor
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "user_id")
    private Long userId;

    /**
     * Stored as a single field including country code.
     * Example: +91-9876543210
     * unique = true ensures no duplicate mobile numbers in the DB.
     */

    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "designation_id")
    private int designationId;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "created_by")
    private int createdBy;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private int status;


    // Automatically set the value before saving to DB
    @PrePersist
    public void setCreatedAt() {
        this.createdDate = LocalDateTime.now();
    }

}