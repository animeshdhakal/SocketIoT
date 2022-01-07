package animesh.app.server.db.model;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import animesh.app.server.db.MainDB;

public class BaseModel {

    public boolean get(String val) {
        try {
            if (MainDB.available()) {
                String sql = "SELECT * FROM " + this.getClass().getAnnotation(Table.class).value() + " WHERE "
                        + this.getClass().getDeclaredFields()[0].getName() + " = ?";
                PreparedStatement stmt = MainDB.getConnection().prepareStatement(sql);
                stmt.setString(1, val);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    for (Field field : this.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        if (field.getType() == int.class) {
                            field.set(this, rs.getInt(field.getName()));
                        } else {
                            field.set(this, rs.getString(field.getName()));
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists() {
        try {
            if (MainDB.available()) {
                String sql = "SELECT * FROM " + this.getClass().getAnnotation(Table.class).value() + " WHERE "
                        + this.getClass().getDeclaredFields()[0].getName() + " = ?";
                System.out.println(sql);
                PreparedStatement stmt = MainDB.getConnection().prepareStatement(sql);
                stmt.setString(1, this.getClass().getDeclaredFields()[0].get(this).toString());
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete() {
        try {
            if (MainDB.available() && this.getClass().isAnnotationPresent(Table.class)) {
                String table = this.getClass().getAnnotation(Table.class).value();
                PreparedStatement stmt = MainDB.getConnection()
                        .prepareStatement("DELETE FROM " + table + " WHERE "
                                + this.getClass().getDeclaredFields()[0].getName() + " = ?");

                stmt.setString(1, this.getClass().getDeclaredFields()[0].get(this).toString());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean save() {
        try {
            if (MainDB.available() && this.getClass().isAnnotationPresent(Table.class)) {
                String table = this.getClass().getAnnotation(Table.class).value();
                StringBuilder query = new StringBuilder("INSERT INTO " + table + " (");
                StringBuilder values = new StringBuilder(" VALUES (");
                for (Field field : this.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getType() == int.class) {
                        query.append(field.getName() + ",");
                        values.append(field.get(this) + ",");
                    } else {
                        query.append(field.getName() + ",");
                        values.append("'" + field.get(this) + "',");
                    }
                }
                query.deleteCharAt(query.length() - 1);
                values.deleteCharAt(values.length() - 1);
                query.append(")");
                values.append(")");
                query.append(values);
                PreparedStatement stmt = MainDB.getConnection().prepareStatement(query.toString());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean update() {
        try {
            if (MainDB.available() && this.getClass().isAnnotationPresent(Table.class)) {
                String table = this.getClass().getAnnotation(Table.class).value();
                StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");
                for (Field field : this.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getType() == int.class) {
                        query.append(field.getName() + " = " + field.get(this) + ",");
                    } else {
                        query.append(field.getName() + " = '" + field.get(this) + "',");
                    }
                }
                query.deleteCharAt(query.length() - 1);
                query.append(" WHERE " + this.getClass().getDeclaredFields()[0].getName() + " = ?");
                PreparedStatement stmt = MainDB.getConnection().prepareStatement(query.toString());
                stmt.setString(1, this.getClass().getDeclaredFields()[0].get(this).toString());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
