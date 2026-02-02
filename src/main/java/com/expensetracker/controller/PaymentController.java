package com.expensetracker.controller;

import com.expensetracker.model.*;
import com.expensetracker.service.PaymentService;
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
 * PaymentController Servlet
 * 
 * CONTROLLER LAYER:
 * - Handles HTTP requests for payment/transaction operations
 * - Routes: /api/payment, /api/payment/record
 * 
 * ACID PROPERTIES:
 * - Payment recording involves multiple updates (transaction + settlement)
 * - In DB implementation, these would be in a single transaction
 */
@WebServlet(urlPatterns = { "/api/payment", "/api/payment/*" })
public class PaymentController extends HttpServlet {

    private PaymentService paymentService;
    private GroupService groupService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        paymentService = new PaymentService();
        groupService = new GroupService();
        gson = new Gson();
    }

    /**
     * GET requests:
     * - /api/payment?userId={id} - Get transactions for a user
     * - /api/payment?groupId={id} - Get transactions for a group
     * - /api/payment/{id} - Get transaction by ID
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
                String userIdParam = request.getParameter("userId");
                String groupIdParam = request.getParameter("groupId");

                List<Transaction> transactions;
                if (groupIdParam != null) {
                    int groupId = Integer.parseInt(groupIdParam);
                    transactions = paymentService.getTransactionsByGroup(groupId);
                } else if (userIdParam != null) {
                    transactions = paymentService.getTransactionsByUser(userIdParam);
                } else {
                    response.setStatus(400);
                    out.print("{\"error\": \"userId or groupId parameter required\"}");
                    return;
                }

                // Enrich with user names
                List<Map<String, Object>> enriched = new ArrayList<>();
                for (Transaction t : transactions) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("transaction", t);
                    User fromUser = groupService.getUser(t.getFromUser());
                    User toUser = groupService.getUser(t.getToUser());
                    item.put("fromUserName", fromUser != null ? fromUser.getName() : "Unknown");
                    item.put("toUserName", toUser != null ? toUser.getName() : "Unknown");
                    enriched.add(item);
                }
                out.print(gson.toJson(enriched));

            } else if (pathInfo.matches("/\\d+")) {
                int transactionId = Integer.parseInt(pathInfo.substring(1));
                Transaction transaction = paymentService.getTransaction(transactionId);
                if (transaction != null) {
                    out.print(gson.toJson(transaction));
                } else {
                    response.setStatus(404);
                    out.print("{\"error\": \"Transaction not found\"}");
                }
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
     * - /api/payment - Record new payment
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String fromUser = request.getParameter("fromUser");
            String toUser = request.getParameter("toUser");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));

            String expenseIdParam = request.getParameter("expenseId");
            Integer expenseId = (expenseIdParam != null && !expenseIdParam.isEmpty())
                    ? Integer.parseInt(expenseIdParam)
                    : null;

            String note = request.getParameter("note");
            if (note == null)
                note = "";

            Transaction transaction = paymentService.recordPayment(
                    fromUser, toUser, amount, expenseId, note);

            response.setStatus(201);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("transaction", transaction);
            result.put("message", "Payment recorded successfully");
            out.print(gson.toJson(result));

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
