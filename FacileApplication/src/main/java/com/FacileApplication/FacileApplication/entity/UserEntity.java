package com.FacileApplication.FacileApplication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fms_user_master", uniqueConstraints = @UniqueConstraint(columnNames = "mobile_number"))
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String user_name;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private Long user_id;

    @Column(nullable = false)
    private String company_name;

    @Column(nullable = false, unique = true)
    private String mobile_number;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer designation_id;

    @Column(nullable = false)
    private String role_id;

    @Column(nullable = false)
    private String role_name;

    @Column(nullable = false)
    private Integer created_by;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer status;

}