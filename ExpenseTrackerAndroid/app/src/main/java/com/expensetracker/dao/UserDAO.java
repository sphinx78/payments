package com.expensetracker.dao;

import com.expensetracker.model.*;
import java.util.List;

/**
 * User Data Access Object Interface
 */
public interface UserDAO {
    int insert(User user);

    User findByPhone(String phoneNumber);

    User findByEmail(String email);

    List<User> findAll();

    void update(User user);

    void delete(String phoneNumber);
}
