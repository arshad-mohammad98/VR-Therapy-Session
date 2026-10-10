package com.vrtherapy.service;

import com.vrtherapy.dto.UserRegistrationDto;
import com.vrtherapy.exception.AuthenticationException;
import com.vrtherapy.model.Patient;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.Therapist;
import com.vrtherapy.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service contract for User identity management, authentication, registration,
 * and administrative account controls.
 */
public interface UserService {

    /**
     * Authenticates a user using BCrypt password hashing.
     */
    User authenticate(String username, String password) throws AuthenticationException;

    /**
     * Registers a new user account with encrypted credentials.
     */
    User registerUser(UserRegistrationDto registrationDto);

    /**
     * Updates an existing user's profile and settings.
     */
    User updateUser(Long id, UserRegistrationDto updateDto);

    /**
     * Toggles an account's active state (activation / deactivation).
     */
    User toggleUserActive(Long id);

    /**
     * Retrieves a user entity by its primary key.
     */
    User getUserById(Long id);

    /**
     * Finds an account by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds an account by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves all registered accounts.
     */
    List<User> getAllUsers();

    /**
     * Filters users by their assigned security role.
     */
    List<User> getUsersByRole(Role role);

    /**
     * Retrieves active patients available for session scheduling.
     */
    List<User> getActivePatients();

    /**
     * Retrieves patients assigned to a specific therapist.
     */
    List<Patient> getPatientsByTherapist(Long therapistId);

    /**
     * Retrieves active therapists.
     */
    List<User> getActiveTherapists();

    List<Therapist> getAllTherapists();

    default List<User> findAllPatients() {
        return getActivePatients();
    }

    default Optional<User> findById(Long id) {
        try {
            return Optional.ofNullable(getUserById(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    long countTotalUsers();

    long countActiveUsers();
}
