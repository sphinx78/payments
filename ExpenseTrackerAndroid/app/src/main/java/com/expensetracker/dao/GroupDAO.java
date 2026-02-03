package com.expensetracker.dao;

import com.expensetracker.model.*;
import java.util.List;

/**
 * Group Data Access Object Interface
 */
public interface GroupDAO {
    int insert(Group group);

    Group findById(int group Id);

    List<Group> findAll();

    List<Group> findByCreator(String creatorId);

    void update(Group group);

    void delete(int groupId);
}
