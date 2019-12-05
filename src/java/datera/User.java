/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datera;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author manojagarwal
 */
@Path("user")
public class User {

    @Context
    private UriInfo context;
    Connection con = null;
    PreparedStatement stm = null;
    ResultSet rs = null;
    private JSONObject mainObj;
    private JSONArray userRegisterArr;
    int userId;
    String fname, lname, address, email, status, password, role, subscriptionType, dateOfBirth, gender;
    long phone;
    User user;

    /**
     * Creates a new instance of User
     */
    public User() {
        try {        
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "mad312team6", "team6pwd");
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Register User - Registers user specifying all its details 
     */
    @GET
    @Path("/registerUser&{fname}&{lname}&{phone}&{password}&{address}&{email}&{role}&{subscriptionType}&{status}&{gender}&{dateOfBirth}")
    @Produces(MediaType.APPLICATION_JSON)
    public String registerUser(@PathParam("fname") String fname,
                               @PathParam("lname") String lname,
                               @PathParam("phone") long phone,
                               @PathParam("password") String password,
                               @PathParam("address") String address,
                               @PathParam("email") String email,
                               @PathParam("role") String role,
                               @PathParam("subscriptionType") String subscriptionType,
                               @PathParam("status") String status,
                               @PathParam("gender") String gender,
                               @PathParam("dateOfBirth") String dateOfBirth)
    {
        ResultSet rs = null;
        JSONObject registerObj = new JSONObject();
        user = new User();
        
                try {
                    String sql;
                    sql = "insert into\n" +
                           "USERDATING (userid, fname, lname, phone, address, email, dateofbirth, gender, status, password, roles, subscriptiontype) \n" +
                           "values (user_id_seq.nextval, ?, ?, ?, ?, '?, TO_DATE(?, 'dd/mm/yyyy'), ?, ?, ?, ?, ?)";
                    stm = con.prepareStatement(sql);
                    stm.setString(1, fname);
                    stm.setString(2, lname);
                    stm.setLong(3, phone);
                    stm.setString(9, password);
                    stm.setString(4, address);
                    stm.setString(5, email);
                    stm.setString(10, role);
                    stm.setString(11, subscriptionType);
                    stm.setString(8, status);
                    stm.setString(7, gender);
                    stm.setString(6, dateOfBirth);
                                       
                    int rs1 = stm.executeUpdate();
                    mainObj = new JSONObject();
                    if(rs1==1)
                    {
                        mainObj.accumulate("Status", "OK");
                        mainObj.accumulate("Timestamp", timeStamp());
                        mainObj.accumulate("message", "User has been successfully registered");
                    }
                } catch (SQLException ex) {
                    mainObj.accumulate("Status", "FAIL");
                    mainObj.accumulate("Timestamp", timeStamp());
                    mainObj.accumulate("message", "Error registering");
                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally{ 
                        if (rs != null) {
                            try {
                                  rs.close();
                                } catch (SQLException e) {
                            /* ignored */
                                }
                            }
                        if (stm != null) {
                            try {
                                    stm.close();
                                } catch (SQLException e) {
                            /* ignored */
                                }
                            }
                        if (con != null) {
                            try {
                                    con.close();
                                } catch (SQLException e) {
                            /* ignored */
                                }
                            }
                }
                return mainObj.toString();
    }
    
    /**
     * USER LIST - Gives the list of the users registered  
     */
    @GET
    @Path("/userlist")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUserList() {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        user = new User();
        
                try {
                    String sql;
                    userRegisterArr = new JSONArray();
                    sql = "select * from userdating";
                    stm = con.prepareStatement(sql);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         userId = rs.getInt(1);
                         fname = rs.getString(2);
                         lname = rs.getString(3);
                         phone = rs.getLong(4);
                         address = rs.getString(5);
                         email = rs.getString(6);
                         dateOfBirth = rs.getString(7);
                         gender = rs.getString(8);
                         status = rs.getString(9);
                         password = rs.getString(10);
                         role = rs.getString(11);
                         subscriptionType = rs.getString(12);
                        
                         mainObj.accumulate("userId", userId);
                         mainObj.accumulate("fname", fname);
                         mainObj.accumulate("lname", lname);
                         mainObj.accumulate("phone", phone);
                         mainObj.accumulate("address", address);
                         mainObj.accumulate("email", email);
                         mainObj.accumulate("dateOfBirth", dateOfBirth);
                         mainObj.accumulate("gender", gender);
                         mainObj.accumulate("status", status);
                         mainObj.accumulate("password", password);
                         mainObj.accumulate("role", role);
                         mainObj.accumulate("subscriptionType", subscriptionType);
                         
                         userRegisterArr.add(mainObj);
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("userList", userRegisterArr);
                } catch (SQLException ex) {
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing users list");
                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally{
                        mainObj.clear();
                        if (rs != null) {
                            try {
                                  rs.close();
                                } catch (SQLException e) {
                            /* ignored */
                                }
                            }
                        if (stm != null) {
                            try {
                                    stm.close();
                                } catch (SQLException e) {
                            /* ignored */
                                }
                            }
                        if (con != null) {
                            try {
                                    con.close();
                                } catch (SQLException e) {
                            /* ignored */
                                }
                            }
                }
        return listObj.toString();
    }
    
    public String timeStamp()
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tm = "" + ts.getTime();
        return tm;
    }
}