package com.expensetracker.controller;

import com.expensetracker.model.*;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.GroupService;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * ExpenseController Servlet
 * 
 * CONTROLLER LAYER:
 * - Handles HTTP requests for expense operations
 * - Routes: /api/expense, /api/expense/add
 * 
 * LAYER SEPARATION:
 * - No SQL or data access logic here
 * - Delegates to ExpenseService for business logic
 */
@WebServlet(urlPatterns = { "/api/expense", "/api/expense/*" })
public class ExpenseController extends HttpServlet {

    private ExpenseService expenseService;
    private GroupService groupService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        expenseService = new ExpenseService();
        groupService = new GroupService();
        gson = new Gson();
    }

    /**
     * GET requests:
     * - /api/expense?groupId={id} - Get expenses for a group
     * - /api/expense/{id} - Get expense by ID
     * - /api/expense/{id}/participants - Get expense participants
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
                // Get expenses by group
                String groupIdParam = request.getParameter("groupId");
                if (groupIdParam != null) {
                    int groupId = Integer.parseInt(groupIdParam);
                    List<Expense> expenses = expenseService.getExpensesByGroup(groupId);

                    // Enrich with payer names
                    List<Map<String, Object>> enriched = new ArrayList<>();
                    for (Expense e : expenses) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("expense", e);
                        User payer = groupService.getUser(e.getPaidBy());
                        item.put("paidByName", payer != null ? payer.getName() : "Unknown");
                        item.put("participants", expenseService.getParticipants(e.getExpenseId()));
                        enriched.add(item);
                    }
                    out.print(gson.toJson(enriched));
                } else {
                    response.setStatus(400);
                    out.print("{\"error\": \"groupId parameter required\"}");
                }
            } else if (pathInfo.matches("/\\d+")) {
                // Get expense by ID
                int expenseId = Integer.parseInt(pathInfo.substring(1));
                Expense expense = expenseService.getExpense(expenseId);
                if (expense != null) {
                    out.print(gson.toJson(expense));
                } else {
                    response.setStatus(404);
                    out.print("{\"error\": \"Expense not found\"}");
                }
            } else if (pathInfo.matches("/\\d+/participants")) {
                // Get expense participants
                int expenseId = Integer.parseInt(pathInfo.split("/")[1]);
                List<ExpenseParticipant> participants = expenseService.getParticipants(expenseId);
                out.print(gson.toJson(participants));
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
     * - /api/expense - Add new expense
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int groupId = Integer.parseInt(request.getParameter("groupId"));
            String paidBy = request.getParameter("paidBy");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String description = request.getParameter("description");
            String splitType = request.getParameter("splitType");
            String[] participantIds = request.getParameterValues("participants");

            Expense expense;

            if ("CUSTOM".equals(splitType)) {
                // Custom split - expect customAmounts parameter
                String[] customAmounts = request.getParameterValues("customAmounts");
                Map<String, BigDecimal> shares = new HashMap<>();

                for (int i = 0; i < participantIds.length; i++) {
                    String userId = participantIds[i];
                    BigDecimal share = new BigDecimal(customAmounts[i]);
                    shares.put(userId, share);
                }

                expense = expenseService.addExpenseCustomSplit(groupId, paidBy, amount,
                        description, shares);
            } else {
                // Equal split
                List<String> participants = Arrays.asList(participantIds);

                expense = expenseService.addExpenseEqualSplit(groupId, paidBy, amount,
                        description, participants);
            }

            response.setStatus(201);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("expense", expense);
            result.put("message", "Expense added successfully");
            out.print(gson.toJson(result));

        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
