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
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author manojagarwal
 */
@Path("action")
public class ActionDating {

    @Context
    private UriInfo context;
    Connection con = null;
    PreparedStatement stm = null;
    ResultSet rs = null;
    private JSONObject mainObj;
    private JSONArray actionDatingArr;
    String actionByName, actionWithName;
    String likeDislike;
    ActionDating actionDating;

    /**
     * Creates a new instance of ActionDating
     */
    public ActionDating() {
        try {        
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "mad312team6", "team6pwd");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Photo List - Gives the list of photos of a user
     */
    @GET
    @Path("/actionUsers&{actionByName}&{actionWithName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAction(@PathParam("actionByName") String theActionByName,
                            @PathParam("actionWithName") String theActionWithName) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        actionDating = new ActionDating();
        
                try {
                    String sql;
                    actionDatingArr = new JSONArray();
                    sql = "select u1.FNAME as actionByName, u2.FNAME as actionWithName, a.likeDislike\n" +
                            "from ACTIONDATING a\n" +
                            "join USERDATING u1 on a.ACTIONBYID = u1.userId\n" +
                            "join USERDATING u2 on a.ACTIONWITHID = u2.userId\n" +
                            "where a.ACTIONBYID = (select userId from USERDATING where fname = ?)\n" +
                            "AND a.ACTIONWITHID = (select userId from USERDATING where fname = ?)";
                    stm = con.prepareStatement(sql);
                    System.out.println("Inside try block");
                    stm.setString(1, theActionByName);
                    stm.setString(2, theActionWithName);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         actionByName = rs.getString(1);
                         actionWithName = rs.getString(2);
                         likeDislike = rs.getString(3);
                        
                         mainObj.accumulate("actionByName", actionByName);
                         mainObj.accumulate("actionWithName", actionWithName);
                         mainObj.accumulate("likeDislike", likeDislike);
                         
                         actionDatingArr.add(mainObj);
                         System.out.println(mainObj);
                         System.out.println("Inside while");
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("Chats", actionDatingArr);
                } catch (SQLException ex) {
                    System.out.println("Inside catch");
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing action");
                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally{
                        System.out.println("Inside finally");
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
    
    /**
     * Add Action - Adding a action
     */
    @GET
    @Path("/addAction&{actionById}&{actionWithId}&{likeDislike}")
    @Produces(MediaType.APPLICATION_JSON)
    public String addAction(@PathParam("actionById") int actionById,
                           @PathParam("actionWithId") int actionWithId,
                           @PathParam("likeDislike") String thePhotoPath)
    {
        ResultSet rs = null;
        actionDating = new ActionDating();
        
                try {
                    String sql;
                    sql = "insert into ACTIONDATING(actionById, actionWithId, likeDislike) values (?, ?, ?)";
                    stm = con.prepareStatement(sql);
                    stm.setInt(1, actionById);
                    stm.setInt(2, actionWithId);
                    stm.setString(3, thePhotoPath);
                                       
                    int rs1 = stm.executeUpdate();
                    mainObj = new JSONObject();
                    if(rs1==1)
                    {
                        mainObj.accumulate("Status", "OK");
                        mainObj.accumulate("Timestamp", timeStamp());
                        mainObj.accumulate("message", "Action added successfully");
                    }
                } catch (SQLException ex) {
                    mainObj.accumulate("Status", "FAIL");
                    mainObj.accumulate("Timestamp", timeStamp());
                    mainObj.accumulate("message", "Error adding action");
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
    
    public String timeStamp()
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tm = "" + ts.getTime();
        return tm;
    }
}
