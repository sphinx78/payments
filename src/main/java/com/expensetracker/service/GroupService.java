package com.expensetracker.service;

import com.expensetracker.dao.*;
import com.expensetracker.dao.impl.*;
import com.expensetracker.model.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GroupService - Business Logic for Group Operations
 * 
 * RESPONSIBILITIES:
 * - Create friend groups
 * - Add/remove members
 * - Retrieve group dashboard data
 * 
 * LAYER: Business Logic Layer
 * DEPENDENCIES: GroupDAO, GroupMemberDAO, UserDAO
 */
public class GroupService {

    private GroupDAO groupDAO;
    private GroupMemberDAO memberDAO;
    private UserDAO userDAO;

    public GroupService() {
        this.groupDAO = new GroupDAOImpl();
        this.memberDAO = new GroupMemberDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Create a new friend group with new user as creator
     * 
     * @param name         Group name
     * @param description  Group description
     * @param creatorName  Creator's name
     * @param creatorPhone Creator's phone (used as user ID)
     * @param creatorEmail Creator's email
     * @return Created group with ID
     */
    public Group createGroup(String name, String description, String creatorName, String creatorPhone,
            String creatorEmail) {
        // Create or find the user
        String userId = createOrFindUser(creatorName, creatorPhone, creatorEmail);

        Group group = new Group(name, description, userId);
        int groupId = groupDAO.insert(group);
        group.setGroupId(groupId);

        // Automatically add creator as member
        addMember(groupId, userId);

        return group;
    }

    /**
     * Create a new user or find existing user by phone number
     * 
     * @param name  User's name
     * @param phone User's phone number (primary key)
     * @param email User's email
     * @return User ID (phone number)
     */
    public String createOrFindUser(String name, String phone, String email) {
        // Check if user already exists
        User existingUser = userDAO.findByPhone(phone);
        if (existingUser != null) {
            return existingUser.getPhoneNumber();
        }

        // Create new user
        User newUser = new User(phone, name, email);
        return userDAO.insert(newUser);
    }

    /**
     * Add a member to a group
     * 
     * @param groupId Group ID
     * @param userId  User ID to add
     * @return true if successful
     */
    public boolean addMember(int groupId, String userId) {
        // Check if already a member
        if (memberDAO.isMember(groupId, userId)) {
            return false;
        }

        GroupMember member = new GroupMember(groupId, userId);
        memberDAO.insert(member);
        return true;
    }

    /**
     * Add a new member to a group with user details
     * Creates the user if doesn't exist
     * 
     * @param groupId     Group ID
     * @param memberName  Member's name
     * @param memberPhone Member's phone (used as user ID)
     * @param memberEmail Member's email
     * @return true if successful, false if already a member
     */
    public boolean addMemberWithNewUser(int groupId, String memberName, String memberPhone, String memberEmail) {
        // Create or find the user
        String userId = createOrFindUser(memberName, memberPhone, memberEmail);

        // Add to group
        return addMember(groupId, userId);
    }

    /**
     * Remove a member from a group
     * 
     * @param groupId Group ID
     * @param userId  User ID to remove
     * @return true if successful
     */
    public boolean removeMember(int groupId, String userId) {
        return memberDAO.delete(groupId, userId);
    }

    /**
     * Get group by ID
     */
    public Group getGroup(int groupId) {
        return groupDAO.findById(groupId);
    }

    /**
     * Get all groups for a user
     */
    public List<Group> getGroupsForUser(String userId) {
        return groupDAO.findByUser(userId);
    }

    /**
     * Get all members of a group
     */
    public List<User> getGroupMembers(int groupId) {
        List<GroupMember> memberships = memberDAO.findByGroup(groupId);
        return memberships.stream()
                .map(gm -> userDAO.findByPhone(gm.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all groups
     */
    public List<Group> getAllGroups() {
        return groupDAO.findAll();
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Get user by ID
     */
    public User getUser(String userId) {
        return userDAO.findByPhone(userId);
    }

    /**
     * Delete a group by ID.
     * Cascading deletes are handled by database foreign key constraints.
     *
     * @param groupId Group ID to delete
     * @return true if the group was deleted
     */
    public boolean deleteGroup(int groupId) {
        return groupDAO.delete(groupId);
    }
}
