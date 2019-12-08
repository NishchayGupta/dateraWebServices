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
@Path("message")
public class MessageDating {

    @Context
    private UriInfo context;
    Connection con = null;
    PreparedStatement stm = null;
    ResultSet rs = null;
    private JSONObject mainObj;
    private JSONArray msgDatingArr;
    int messageId, senderUsrId, receiveUsrId, delivered;
    String chat, senderUsrName, receiveUsrName;
    String timeMessage;
    MessageDating msgDating;
    
    /**
     * Creates a new instance of MessageDating
     */
    public MessageDating() {
        try {        
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "mad312team6", "team6pwd");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Chat History - Gives the list of chats between sender and receiver
     */
    @GET
    @Path("/chatHistory&{senderUserName}&{receiveUserName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getChatHistory(@PathParam("senderUserName") String senderUserName,
                                 @PathParam("receiveUserName") String receiveUserName) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        msgDating = new MessageDating();
        
                try {
                    String sql;
                    msgDatingArr = new JSONArray();
                    sql = "select MESSAGEDATING.delivered, MESSAGEDATING.chat, MESSAGEDATING.timemessage, MESSAGEDATING.messageId from MESSAGEDATING where MESSAGEDATING.senderUserId = (select userId from USERDATING where fname = ? AND ROWNUM <= 1) AND MESSAGEDATING.receiveUserId = (select userId from USERDATING where fname = ? AND ROWNUM <= 1)";
                    stm = con.prepareStatement(sql);
                    System.out.println("Inside try block");
                    stm.setString(1, senderUserName);
                    stm.setString(2, receiveUserName);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         messageId = rs.getInt(4);
                         senderUsrName = senderUserName;
                         receiveUsrName = receiveUserName;
                         delivered = rs.getInt(1);
                         chat = rs.getString(2);
                         timeMessage = rs.getString(3);
                         
                        
                         mainObj.accumulate("messageId", messageId);
                         mainObj.accumulate("senderUserName", senderUsrName);
                         mainObj.accumulate("receiveUserName", receiveUsrName);
                         mainObj.accumulate("delivered", delivered);
                         mainObj.accumulate("chat", chat);
                         mainObj.accumulate("timeMessage", timeMessage);
                         
                         msgDatingArr.add(mainObj);
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("Chats", msgDatingArr);
                } catch (SQLException ex) {
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing chats between users");
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
        return listObj.toString();
    }
    
    /**
     * Delivery Chat Status - Tells us if message was delivered or not
     */
    @GET
    @Path("/deliveryChatStatus&{senderUserName}&{receiveUserName}&{chat}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getdeliveryStatus(@PathParam("senderUserName") String theSenderUserName,
                                    @PathParam("receiveUserName") String theReceiveUserName,
                                    @PathParam("chat") String theChat) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        msgDating = new MessageDating();
        
                try {
                    String sql;
                    msgDatingArr = new JSONArray();
                    sql = "select u1.FNAME as senderUserName, u2.FNAME as receiveUserName, m.delivered, m.messageId, m.chat\n" +
                            "from MESSAGEDATING m\n" +
                            "join USERDATING u1 on m.SENDERUSERID = u1.userId\n" +
                            "join USERDATING u2 on m.RECEIVEUSERID = u2.userId\n" +
                            "where m.SENDERUSERID = (select userId from USERDATING where fname = ?)\n" +
                            "AND m.RECEIVEUSERID = (select userId from USERDATING where fname = ?)\n" +
                            "AND m.chat = ?";
                    stm = con.prepareStatement(sql);
                    stm.setString(1, theSenderUserName);
                    stm.setString(2, theReceiveUserName);
                    stm.setString(3, theChat);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         messageId = rs.getInt(4);
                         senderUsrName = rs.getString(1);
                         receiveUsrName = rs.getString(2);
                         delivered = rs.getInt(3);
                         chat = rs.getString(5);
                        
                         mainObj.accumulate("messageId", messageId);
                         mainObj.accumulate("senderUserName", senderUsrName);
                         mainObj.accumulate("receiveUserName", receiveUsrName);
                         if(delivered == 1)
                            mainObj.accumulate("delivered", "delivered");
                         else
                            mainObj.accumulate("delivered", "not delivered");
                         mainObj.accumulate("chat", chat);
                         msgDatingArr.add(mainObj);
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("Chat", msgDatingArr);
                } catch (SQLException ex) {
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing delivery status of chat between users");
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
    
    /**
     * Chat between Users - Shows which two users had a chat message
     */
    @GET
    @Path("/chatBetweenUsers&{chat}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getchatUsers(@PathParam("chat") String theChat) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        msgDating = new MessageDating();
        
                try {
                    String sql;
                    msgDatingArr = new JSONArray();
                    sql = "select u1.FNAME as senderUserName, u2.FNAME as receiveUserName, m.messageId, m.chat\n" +
                            "from MESSAGEDATING m\n" +
                            "join USERDATING u1 on m.SENDERUSERID = u1.userId\n" +
                            "join USERDATING u2 on m.RECEIVEUSERID = u2.userId\n" +
                            "where m.chat = ?";
                    stm = con.prepareStatement(sql);
                    stm.setString(1, theChat);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         messageId = rs.getInt(3);
                         senderUsrName = rs.getString(1);
                         System.out.println("Testing: " + rs.getString(1));
                         receiveUsrName = rs.getString(2);
                         chat = rs.getString(4);
                        
                         mainObj.accumulate("messageId", messageId);
                         mainObj.accumulate("senderUserName", senderUsrName);
                         mainObj.accumulate("receiveUserName", receiveUsrName);
                         mainObj.accumulate("chat", chat);
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("messageId", mainObj.getInt("messageId"));
                    listObj.accumulate("senderUserName", mainObj.getString("senderUserName"));
                    listObj.accumulate("receiveUserName", mainObj.getString("receiveUserName"));
                    listObj.accumulate("chat", mainObj.getString("chat"));
                } catch (SQLException ex) {
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing which two users had a chat");
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
    
    /**
     * Chat receiver - Shows who is the receiver of a chat message
     */
    @GET
    @Path("/chatReceiver&{senderUserName}&{chat}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getReceiver(@PathParam("senderUserName") String theSenderUserName,
                              @PathParam("chat") String theChat) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        msgDating = new MessageDating();
        
                try {
                    String sql;
                    msgDatingArr = new JSONArray();
                    sql = "select u1.FNAME as senderUserName, u2.FNAME as receiveUserName, m.messageId, m.chat\n" +
                            "from MESSAGEDATING m\n" +
                            "join USERDATING u1 on m.SENDERUSERID = u1.userId\n" +
                            "join USERDATING u2 on m.RECEIVEUSERID = u2.userId\n" +
                            "where m.SENDERUSERID = (select userId from USERDATING where fname = ?)\n" +
                            "AND m.chat = ?";
                    stm = con.prepareStatement(sql);
                    stm.setString(1, theSenderUserName);
                    stm.setString(2, theChat);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         messageId = rs.getInt(3);
                         senderUsrName = rs.getString(1);
                         receiveUsrName = rs.getString(2);
                        
                         mainObj.accumulate("messageId", messageId);
                         mainObj.accumulate("senderUserName", senderUsrName);
                         mainObj.accumulate("receiveUserName", receiveUsrName);
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("messageId", mainObj.getInt("messageId"));
                    listObj.accumulate("senderUserName", mainObj.getString("senderUserName"));
                    listObj.accumulate("receiveUserName", mainObj.getString("receiveUserName"));
                    //listObj.accumulate("messageReceiver", mainObj);
                } catch (SQLException ex) {
                    listObj.accumulate("Status", "FAIL");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("message", "Error in showing receiver of a chat message");
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
        return listObj.toString();
    }
    
    /**
     * Add Message - Adding a message/ Sending a message
     */
    @GET
    @Path("/addMessage&{senderUserId}&{receiveUserId}&{delivered}&{chat}")
    @Produces(MediaType.APPLICATION_JSON)
    public String addMessage(@PathParam("senderUserId") int theSenderUsrId,
                             @PathParam("receiveUserId") int theReceiveUsrId,
                             @PathParam("delivered") int theDelivered,
                             @PathParam("chat") String theChat)
    {
        ResultSet rs = null;
        JSONObject addMsgObj = new JSONObject();
        msgDating = new MessageDating();
        
                try {
                    String sql;
                    sql = "Insert into\n" +
                            "MESSAGEDATING (messageId, senderUserId, receiveUserId, delivered, chat, timemessage) \n" +
                            "values (message_id_seq.nextval, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                    stm = con.prepareStatement(sql);
                    stm.setInt(1, theSenderUsrId);
                    stm.setInt(2, theReceiveUsrId);
                    stm.setInt(3, theDelivered);
                    stm.setString(4, theChat);
                                       
                    int rs1 = stm.executeUpdate();
                    mainObj = new JSONObject();
                    if(rs1==1)
                    {
                        mainObj.accumulate("Status", "OK");
                        mainObj.accumulate("Timestamp", timeStamp());
                        mainObj.accumulate("message", "Message added successfully");
                    }
                } catch (SQLException ex) {
                    mainObj.accumulate("Status", "FAIL");
                    mainObj.accumulate("Timestamp", timeStamp());
                    mainObj.accumulate("message", "Error adding message");
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