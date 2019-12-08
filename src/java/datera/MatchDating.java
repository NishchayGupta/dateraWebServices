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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
@Path("match")
public class MatchDating {

    @Context
    private UriInfo context;
    Connection con = null;
    PreparedStatement stm = null;
    ResultSet rs = null;
    private JSONObject mainObj;
    private JSONArray matchDatingArr;
    int matchById, matchWithId;
    String matchByName, matchWithName;
    String match_date;
    MatchDating matchDating;
    /**
     * Creates a new instance of MatchDating
     */
    public MatchDating() {
        try {        
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "mad312team6", "team6pwd");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Match History - Gives the list of match of a user
     */
    @GET
    @Path("/matchHistory&{fname}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMatchHistory(@PathParam("fname") String theFName) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        matchDating = new MatchDating();
        
                try {
                    String sql;
                    matchDatingArr = new JSONArray();
                    sql = "select u1.FNAME as matchBy, u2.FNAME as matchWith, m.match_date\n" +
                            "from MATCHDATING m\n" +
                            "join USERDATING u1 on m.MATCHEDBYID = u1.USERID\n" +
                            "join USERDATING u2 on m.MATCHEDWITHID = u2.USERID\n" +
                            "where m.MATCHEDBYID = (select userId from USERDATING where fname = ?)";
                    stm = con.prepareStatement(sql);
                    System.out.println("Inside try block");
                    stm.setString(1, theFName);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         matchByName = rs.getString(1);
                         matchWithName = rs.getString(2);
                         match_date = rs.getString(3);
                        
                         mainObj.accumulate("matchByName", matchByName);
                         mainObj.accumulate("matchWithName", matchWithName);
                         mainObj.accumulate("match_date", match_date);
                         
                         matchDatingArr.add(mainObj);
                         System.out.println(mainObj);
                         System.out.println("Inside while");
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("Match", matchDatingArr);
                } catch (SQLException ex) {
                    System.out.println("Inside catch");
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing match for " + theFName);
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
     * Match Date - Gives the date of the match
     */
    @GET
    @Path("/matchDate&{matchByName}&{matchWithName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMatchDate(@PathParam("matchByName") String theMatchByName,
                                  @PathParam("matchWithName") String theMatchWithName) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        matchDating = new MatchDating();
        
                try {
                    String sql;
                    matchDatingArr = new JSONArray();
                    sql = "select u1.FNAME as matchedByName, u2.FNAME as matchedWithName, m.match_date\n" +
                            "from MATCHDATING m\n" +
                            "join USERDATING u1 on m.MATCHEDBYID = u1.userId\n" +
                            "join USERDATING u2 on m.MATCHEDWITHID = u2.userId\n" +
                            "where m.MATCHEDBYID = (select userId from USERDATING where fname = ?)\n" +
                            "AND m.MATCHEDWITHID = (select userId from USERDATING where fname = ?)";
                    stm = con.prepareStatement(sql);
                    System.out.println("Inside try block");
                    stm.setString(1, theMatchByName);
                    stm.setString(2, theMatchWithName);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         matchByName = rs.getString(1);
                         matchWithName = rs.getString(2);
                         match_date = rs.getString(3);
                        
                         mainObj.accumulate("matchByName", matchByName);
                         mainObj.accumulate("matchWithName", matchWithName);
                         mainObj.accumulate("match_date", match_date);
                         
                         matchDatingArr.add(mainObj);
                         
                         System.out.println("Inside while");
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("Chats", matchDatingArr);
                } catch (SQLException ex) {
                    System.out.println("Inside catch");
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing match date");
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
     * Add Match - Adding a match/ Sending a match request
     */
    @GET
    @Path("/addMatch&{matchById}&{matchWithId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String addMatch(@PathParam("matchById") int theMatchById,
                           @PathParam("matchWithId") int theMatchWithId)
    {
        ResultSet rs = null;
        matchDating = new MatchDating();
        
                try {
                    String sql;
                    sql = "insert into MATCHDATING(matchedById, matchedWithId, match_date) values (?, ?, TO_DATE(?, 'dd/mm/yyyy'))";
                    stm = con.prepareStatement(sql);
                    stm.setInt(1, theMatchById);
                    stm.setInt(2, theMatchWithId);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    System.out.println(formatter.format(calendar.getTime()));
                    stm.setString(3, formatter.format(calendar.getTime()));
                                       
                    int rs1 = stm.executeUpdate();
                    mainObj = new JSONObject();
                    if(rs1==1)
                    {
                        mainObj.accumulate("Status", "OK");
                        mainObj.accumulate("Timestamp", timeStamp());
                        mainObj.accumulate("message", "Match added successfully");
                    }
                } catch (SQLException ex) {
                    mainObj.accumulate("Status", "FAIL");
                    mainObj.accumulate("Timestamp", timeStamp());
                    mainObj.accumulate("message", "Error adding match");
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