package com.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class TestServlet extends HttpServlet {
    DBConnection db = new DBConnection();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String name = request.getParameter("studentName");
        String email = request.getParameter("studentEmail");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");

        try (Connection con = db.getConnection()) {
            if ("Insert".equalsIgnoreCase(action)) {
                String sql = "INSERT INTO MYSTUDENT (name, email) VALUES (?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, name);
                    pst.setString(2, email);
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        out.println("<h2>Insert Successful</h2>");
                    } else {
                        out.println("<h2>Insert Failed</h2>");
                    }
                }
            } else if ("View".equalsIgnoreCase(action)) {
                String sql ="SELECT * FROM MYSTUDENT";
                try (PreparedStatement pst = con.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {
                    out.println("<h2>Student List:</h2>");
                    out.println("<table border='1'><tr><th>Name</th><th>Email</th></tr>");
                    while (rs.next()) {
                        out.println("<tr><td>" + rs.getString("name") + "</td><td>" + rs.getString("email") + "</td></tr>");
                    }
                    out.println("</table>");
                }
            } else {
                out.println("<h2>Unknown Action: " + action + "</h2>");
            }
        } catch (SQLException e) {
            e.printStackTrace(out);
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }

        out.println("</body></html>");
    }
}
