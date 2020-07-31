/*
 * Copyright (C) 2019 Rodolfo Navarro B.
 *
 * This program is free software: you can redistribute it and/or modify
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 */
package co.cr.ACAT;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.xml.sax.SAXException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author Rodolfo Navarro
 */
public class SQLite_Mantain {
    Connection conexion =null;
    Statement stmt =null;
  
    public void OpenDB(){
        try {
          Class.forName("org.sqlite.JDBC");
         }catch (ClassNotFoundException e) {
           //JOptionPane.showMessageDialog(null, e.getMessage());
         }  
          try {
           conexion = DriverManager.getConnection("jdbc:sqlite:"+"dictionary.db");
          }catch (SQLException e) {
          //JOptionPane.showMessageDialog(null, e.getMessage());
          }
    }
    
    public void CloseDB(){
        try {
            conexion.close();
        }catch (SQLException ex) {
            
        }
    }
    
    public JSONArray execute_sql(String commandSQL) throws SAXException, SQLException,IOException, ClassNotFoundException{
        JSONArray ja_data = new JSONArray();
        Grlcnf grd = new Grlcnf();
        Grlcnf.QueryType queryType;
        queryType = grd.getQueryType(commandSQL);
        OpenDB();
        Statement statement = conexion.createStatement();
        ResultSet rs;
        JSONObject row;
        int records;
        switch (queryType) {
            case update:
                row = new JSONObject();
                records=statement.executeUpdate(commandSQL);
                if(!conexion.getAutoCommit()){
                    conexion.commit();
                }
                row.put("rowsAffected", records);
                ja_data.add(row);
                break;
            case delete:
                row = new JSONObject();
                records=statement.executeUpdate(commandSQL);
                if(!conexion.getAutoCommit()){
                    conexion.commit();
                }
                row.put("rowsAffected", records);
                ja_data.add(row);
                break;
            case insert:
                row = new JSONObject();
                records=statement.executeUpdate(commandSQL);
                if(!conexion.getAutoCommit()){
                    conexion.commit();
                }
                row.put("rowsAffected", records);
                ja_data.add(row);
                break;
            case select:
            case show:
                rs =statement.executeQuery(commandSQL);
                while(rs.next()){
                    int total_cols = rs.getMetaData().getColumnCount();
                    row = new JSONObject();
                    for (int i = 0; i < total_cols; i++){
                        row.put(rs.getMetaData().getColumnLabel(i+1), rs.getObject(i+1));
                    }
                    ja_data.add(row);
                }
                break;
            }
        CloseDB();
        return ja_data;
    }
}


