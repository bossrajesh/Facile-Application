package com.fms.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * FMS User Master Entity
 * ----------------------
 * This maps to the `fms_user_master` table in MySQL.
 *
 * Think of this as a "blueprint" for storing user information.
 * Every time a new user registers, a row gets added to this table.
 */
@Entity
@Table(
    name = "fms_user_master",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "mobile_number", name = "uk_mobile_number"),
        @UniqueConstraint(columnNames = "email", name = "uk_email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMaster {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    /** Full name of the user */
    @Column(name = "user_name", nullable = false, length = 100)
    private String username;

    /** User's email address — must be unique */
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** User's mobile number — must be unique, used for OTP login */
    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    /** User's Company Name */
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    /** User's Designation */
    @Column(name = "designation", nullable = false, length = 150)
    private String designation;

    /** Designation Id of the user */
    @Column(name = "designation_id")
    private Integer designationId;

    /** Role Id of the user */
    @Column(name = "role_id")
    private String roleId;

    /** Created By for the user */
    @Column(name = "created_by")
    private Integer createdBy;

    /** Role Name of the user */
    @Column(name = "role_name")
    private String roleName;

    /** password of the user */
    @Column(name = "password")
    private String password;

    /** Account status: ACTIVE / INACTIVE */
    @Column(name = "status", length = 20)
    @Builder.Default
    private Integer status = 1;

    /** Timestamp when the record was created */
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdAt;

}
