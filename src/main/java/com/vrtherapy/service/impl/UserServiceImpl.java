package com.vrtherapy.service.impl;

import com.vrtherapy.dao.UserDao;
import com.vrtherapy.dao.impl.UserDaoImpl;
import com.vrtherapy.dto.UserRegistrationDto;
import com.vrtherapy.exception.AuthenticationException;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.Admin;
import com.vrtherapy.model.Patient;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.Therapist;
import com.vrtherapy.model.User;
import com.vrtherapy.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for user lifecycle management and BCrypt credential security.
 *
 * Demonstrates:
 * - Clean Service Layer Architecture
 * - BCrypt Password Hashing and Authentication
 * - Polymorphic entity handling
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User authenticate(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Username and password must not be empty.");
        }

        User user = userDao.findByUsername(username.trim())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!user.isActive()) {
            throw new AuthenticationException("Account is currently deactivated. Please contact support.");
        }

        boolean passwordMatch = false;
        try {
            if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$") || user.getPassword().startsWith("$2y$")) {
                passwordMatch = BCrypt.checkpw(password, user.getPassword());
            } else {
                passwordMatch = user.getPassword().equals(password);
            }
        } catch (Exception ex) {
            logger.warn("BCrypt verification exception for user {}: {}", username, ex.getMessage());
            passwordMatch = user.getPassword().equals(password);
        }

        if (!passwordMatch) {
            throw new AuthenticationException("Invalid username or password.");
        }

        logger.info("User [{}] (Role={}) authenticated successfully.", user.getUsername(), user.getRole());
        return user;
    }

    @Override
    public User registerUser(UserRegistrationDto dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty.");
        }
        if (userDao.existsByUsername(dto.getUsername().trim())) {
            throw new ValidationException("Username '" + dto.getUsername() + "' is already registered.");
        }
        if (userDao.existsByEmail(dto.getEmail().trim())) {
            throw new ValidationException("Email '" + dto.getEmail() + "' is already in use.");
        }

        String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(10));
        Role role = dto.getRole() != null ? dto.getRole() : Role.PATIENT;

        User user;
        if (role == Role.PATIENT) {
            Patient p = new Patient();
            p.setAssignedTherapistId(dto.getAssignedTherapistId());
            p.setEmergencyContact(dto.getEmergencyContact());
            p.setBaselineAnxiety(dto.getBaselineAnxiety() != null ? dto.getBaselineAnxiety() : 5);
            user = p;
        } else if (role == Role.THERAPIST) {
            Therapist t = new Therapist();
            t.setLicenseNumber(dto.getLicenseNumber());
            t.setSpecialization(dto.getSpecialization());
            user = t;
        } else {
            Admin a = new Admin();
            a.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : "Administration");
            user = a;
        }

        user.setUsername(dto.getUsername().trim());
        user.setPassword(hashedPassword);
        user.setFullName(dto.getFullName() != null ? dto.getFullName().trim() : dto.getUsername());
        user.setEmail(dto.getEmail().trim());
        user.setRole(role);
        user.setActive(true);
        user.setPhone(dto.getPhone());
        user.setClinicalBioOrNotes(dto.getClinicalBioOrNotes());

        return userDao.save(user);
    }

    @Override
    public User updateUser(Long id, UserRegistrationDto dto) {
        User user = getUserById(id);
        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName().trim());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail().trim());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone().trim());
        }
        if (dto.getClinicalBioOrNotes() != null) {
            user.setClinicalBioOrNotes(dto.getClinicalBioOrNotes().trim());
        }

        if (user instanceof Patient p) {
            if (dto.getAssignedTherapistId() != null) p.setAssignedTherapistId(dto.getAssignedTherapistId());
            if (dto.getEmergencyContact() != null) p.setEmergencyContact(dto.getEmergencyContact());
            if (dto.getBaselineAnxiety() != null) p.setBaselineAnxiety(dto.getBaselineAnxiety());
        } else if (user instanceof Therapist t) {
            if (dto.getLicenseNumber() != null) t.setLicenseNumber(dto.getLicenseNumber());
            if (dto.getSpecialization() != null) t.setSpecialization(dto.getSpecialization());
        } else if (user instanceof Admin a) {
            if (dto.getDepartment() != null) a.setDepartment(dto.getDepartment());
        }

        userDao.update(user);
        return user;
    }

    @Override
    public User toggleUserActive(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        userDao.update(user);
        logger.info("Toggled active status for user {}: now {}", id, user.isActive());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userDao.findByRole(role);
    }

    @Override
    public List<User> getActivePatients() {
        return userDao.findByRole(Role.PATIENT).stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> getPatientsByTherapist(Long therapistId) {
        return userDao.findPatientsByTherapistId(therapistId);
    }

    @Override
    public List<User> getActiveTherapists() {
        return userDao.findByRole(Role.THERAPIST).stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Therapist> getAllTherapists() {
        return userDao.findAllTherapists();
    }

    @Override
    public long countTotalUsers() {
        return userDao.count();
    }

    @Override
    public long countActiveUsers() {
        return userDao.countActiveUsers();
    }
}
