package com.expensetracker.controller;

import com.expensetracker.model.*;
import com.expensetracker.service.GroupService;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * GroupController Servlet
 * 
 * CONTROLLER LAYER:
 * - Handles HTTP requests for group operations
 * - Routes: /api/group, /api/group/members, /api/group/create
 * - Returns JSON responses
 * 
 * LAYER SEPARATION:
 * - No SQL or data access logic here
 * - Delegates to GroupService for business logic
 */
@WebServlet(urlPatterns = { "/api/group", "/api/group/*" })
public class GroupController extends HttpServlet {

    private GroupService groupService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        groupService = new GroupService();
        gson = new Gson();
    }

    /**
     * GET requests:
     * - /api/group - Get all groups
     * - /api/group/{id} - Get group by ID
     * - /api/group/{id}/members - Get group members
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all groups
                List<Group> groups = groupService.getAllGroups();
                out.print(gson.toJson(groups));
            } else if (pathInfo.equals("/users")) {
                // Get all users
                List<User> users = groupService.getAllUsers();
                out.print(gson.toJson(users));
            } else if (pathInfo.matches("/\\d+")) {
                // Get group by ID
                int groupId = Integer.parseInt(pathInfo.substring(1));
                Group group = groupService.getGroup(groupId);
                if (group != null) {
                    out.print(gson.toJson(group));
                } else {
                    response.setStatus(404);
                    out.print("{\"error\": \"Group not found\"}");
                }
            } else if (pathInfo.matches("/\\d+/members")) {
                // Get group members
                int groupId = Integer.parseInt(pathInfo.split("/")[1]);
                List<User> members = groupService.getGroupMembers(groupId);
                out.print(gson.toJson(members));
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"Invalid request\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * POST requests:
     * - /api/group - Create new group (with new user as creator)
     * - /api/group/{id}/members - Add member to group (with new user details)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Create new group with new user as creator
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String creatorName = request.getParameter("creatorName");
                String creatorPhone = request.getParameter("creatorPhone");
                String creatorEmail = request.getParameter("creatorEmail");

                Group group = groupService.createGroup(name, description, creatorName, creatorPhone, creatorEmail);
                response.setStatus(201);
                out.print(gson.toJson(group));
            } else if (pathInfo.matches("/\\d+/members")) {
                // Add member to group with new user details
                int groupId = Integer.parseInt(pathInfo.split("/")[1]);
                String memberName = request.getParameter("memberName");
                String memberPhone = request.getParameter("memberPhone");
                String memberEmail = request.getParameter("memberEmail");

                boolean success = groupService.addMemberWithNewUser(groupId, memberName, memberPhone, memberEmail);
                Map<String, Object> result = new HashMap<>();
                result.put("success", success);
                result.put("message", success ? "Member added successfully" : "Member already exists in this group");
                out.print(gson.toJson(result));
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"Invalid request\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
