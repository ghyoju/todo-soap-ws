package com.example.todoservice;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hsqldb.jdbc.JDBCDataSource;

@Stateless
@WebService(
    portName = "TodoPort",
    serviceName = "TodoService",
    targetNamespace = "http://todoservice.example.com/",
    endpointInterface = "com.example.todoservice.TodoService",
    wsdlLocation = "WEB-INF/wsdl/TodoService.wsdl"
)
public class TodoServiceImpl implements TodoService {
    
    @Resource
    private WebServiceContext context;
    
    private DataSource getDataSource() {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setDatabase("jdbc:hsqldb:mem:todo");
        ds.setUser("SA");
        ds.setPassword("");
        return ds;
    }
    
    private void initializeDatabase() {
        try (Connection conn = getDataSource().getConnection()) {
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS todos (id INTEGER IDENTITY, task VARCHAR(255))"
            );
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    @Override
    public String addTodo(String task) {
        initializeDatabase();
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO todos (task) VALUES (?)", 
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, task);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return "Task added with ID: " + rs.getInt(1);
            }
            return "Task added successfully";
        } catch (SQLException e) {
            return "Error adding task: " + e.getMessage();
        }
    }

    @Override
    public String getTodo(int id) {
        initializeDatabase();
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT task FROM todos WHERE id = ?")) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return "Task " + id + ": " + rs.getString("task");
            }
            return "Task not found";
        } catch (SQLException e) {
            return "Error retrieving task: " + e.getMessage();
        }
    }
}