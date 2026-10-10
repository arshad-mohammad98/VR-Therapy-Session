package com.vrtherapy.dao;

import com.vrtherapy.model.Patient;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.Therapist;
import com.vrtherapy.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for polymorphic User entities.
 */
public interface UserDao extends GenericDao<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<Patient> findPatientsByTherapistId(Long therapistId);

    List<Therapist> findAllTherapists();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    long countActiveUsers();
}
