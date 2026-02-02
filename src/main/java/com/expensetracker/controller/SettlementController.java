package com.expensetracker.controller;

import com.expensetracker.model.*;
import com.expensetracker.service.SettlementService;
import com.expensetracker.service.SettlementService.*;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * SettlementController Servlet
 * 
 * CONTROLLER LAYER:
 * - Handles HTTP requests for settlement/balance operations
 * - Routes: /api/settlement, /api/settlement/summary
 */
@WebServlet(urlPatterns = { "/api/settlement", "/api/settlement/*" })
public class SettlementController extends HttpServlet {

    private SettlementService settlementService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        settlementService = new SettlementService();
        gson = new Gson();
    }

    /**
     * GET requests:
     * - /api/settlement?groupId={id} - Get all settlements for a group
     * - /api/settlement/pending?groupId={id} - Get pending settlements
     * - /api/settlement/settled?groupId={id} - Get settled settlements
     * - /api/settlement/summary?groupId={id} - Get formatted summary
     * - /api/settlement/balances?groupId={id} - Get member balance summary
     * - /api/settlement/simplify?groupId={id} - Get simplified debt summary
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        String groupIdParam = request.getParameter("groupId");

        try {
            if (groupIdParam == null) {
                response.setStatus(400);
                out.print("{\"error\": \"groupId parameter required\"}");
                return;
            }

            int groupId = Integer.parseInt(groupIdParam);

            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all settlements for group
                List<Settlement> settlements = settlementService.getAllSettlements(groupId);
                out.print(gson.toJson(settlements));

            } else if (pathInfo.equals("/pending")) {
                // Get pending settlements
                List<Settlement> pending = settlementService.getPendingSettlements(groupId);
                out.print(gson.toJson(pending));

            } else if (pathInfo.equals("/settled")) {
                // Get settled settlements
                List<Settlement> settled = settlementService.getSettledSettlements(groupId);
                out.print(gson.toJson(settled));

            } else if (pathInfo.equals("/summary")) {
                // Get formatted settlement summary
                List<SettlementSummary> summary = settlementService.getSettlementSummary(groupId);
                out.print(gson.toJson(summary));

            } else if (pathInfo.equals("/balances")) {
                // Get member balance summary
                List<UserBalance> balances = settlementService.getGroupBalanceSummary(groupId);
                out.print(gson.toJson(balances));

            } else if (pathInfo.equals("/simplify")) {
                // Get simplified debt summary
                List<SettlementSummary> simplified = settlementService.simplifyDebts(groupId);
                out.print(gson.toJson(simplified));

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
