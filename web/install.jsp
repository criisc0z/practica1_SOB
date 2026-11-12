<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Database SQL Load</title>
        <style>
            .error { color: red; }
            pre { color: green; }
        </style>
    </head>
    <body>
        <h2>Database SQL Load</h2>
        <%
            // How to customize:
            // 1. Update the database name on dbname.
            // 2. Tables are created by JPA (drop-and-create): CUSTOMER, TOOL, PROMPT.
            // 3. No table definitions needed for the same reason.
            // 4. The data is under data[]. It exits at the very first error.
            String dbname = "SOB_grup_60";
            String schema = "ROOT";
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/" + dbname, "root", "root");
            Statement stmt = con.createStatement();

            // privat is a SMALLINT: 0 = public, 1 = private
            String data[] = new String[]{
                "INSERT INTO " + schema + ".CUSTOMER (ID, CREATIONDATE, EMAIL, PASSWORD, USERNAME) VALUES (NEXT VALUE FOR CUSTOMER_GEN, CURRENT_TIMESTAMP, 'sob@sob.cat', 'sob', 'sob')",
                "INSERT INTO " + schema + ".CUSTOMER (ID, CREATIONDATE, EMAIL, PASSWORD, USERNAME) VALUES (NEXT VALUE FOR CUSTOMER_GEN, CURRENT_TIMESTAMP, 'ana@sob.cat', 'ana123', 'ana')",

                "INSERT INTO " + schema + ".TOOL (ID, CATEGORY, CODE, CREATIONDATE, DESCRIPTION, LANGUAGE, NAME, PRIVAT, VERSION, AUTHOR_ID) VALUES (NEXT VALUE FOR TOOL_GEN, 'math', 'def suma(a, b): return a + b', CURRENT_TIMESTAMP, 'Suma dos numeros', 'python', 'suma', 0, '1.0', 1)",
                "INSERT INTO " + schema + ".TOOL (ID, CATEGORY, CODE, CREATIONDATE, DESCRIPTION, LANGUAGE, NAME, PRIVAT, VERSION, AUTHOR_ID) VALUES (NEXT VALUE FOR TOOL_GEN, 'text', 'def upper(s): return s.upper()', CURRENT_TIMESTAMP, 'Pasa a mayusculas', 'python', 'upper', 1, '1.0', 2)",

                "INSERT INTO " + schema + ".PROMPT (ID, DATACREACIO, DESCRIPTION, PRIVAT, TEMPLATE, TITOL, AUTOR_ID) VALUES (NEXT VALUE FOR PROMPT_GEN, CURRENT_TIMESTAMP, 'Resume un texto', 0, 'Resume el siguiente texto: {text}', 'Resumidor', 1)",
                "INSERT INTO " + schema + ".PROMPT (ID, DATACREACIO, DESCRIPTION, PRIVAT, TEMPLATE, TITOL, AUTOR_ID) VALUES (NEXT VALUE FOR PROMPT_GEN, CURRENT_TIMESTAMP, 'Prompt privado', 1, 'Eres un experto en {topic}', 'Experto', 2)"
            };

            for (String datum : data) {
                if (stmt.executeUpdate(datum) <= 0) {
                    out.println("<span class='error'>Error inserting data: " + datum + "</span>");
                    return;
                }
                out.println("<pre> -> " + datum + "</pre>");
            }
            con.close();
        %>
        <button onclick="window.location='<%=request.getSession().getServletContext().getContextPath()%>'">Go home</button>
    </body>
</html>
