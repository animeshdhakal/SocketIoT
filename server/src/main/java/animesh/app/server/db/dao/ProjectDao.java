package animesh.app.server.db.dao;

import java.sql.PreparedStatement;

import animesh.app.server.db.MainDB;
import animesh.app.server.db.model.Project;

public class ProjectDao {

    public boolean createProject(Project project) {
        PreparedStatement stmt = null;
        if (MainDB.available()) {
            try {
                stmt = MainDB.conn
                        .prepareStatement("INSERT INTO projects (name, token, json, user_id) VALUES (?, ?, ?, ?)");
                stmt.setString(1, project.name);
                stmt.setString(2, project.token);
                stmt.setString(3, project.json);
                stmt.setInt(4, project.userId);
                stmt.executeUpdate();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
