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
@Path("photo")
public class PhotoDating {

    @Context
    private UriInfo context;
    Connection con = null;
    PreparedStatement stm = null;
    ResultSet rs = null;
    private JSONObject mainObj;
    private JSONArray photoDatingArr;
    int photoId, uploaderUserId;
    String photoPath;
    PhotoDating photoDating;
    
    /**
     * Creates a new instance of PhotoDating
     */
    public PhotoDating() {
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
    @Path("/singlePhotoList&{fname}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMatchHistory(@PathParam("fname") String theFName) {
        ResultSet rs = null;
        JSONObject listObj = new JSONObject();
        photoDating = new PhotoDating();
        
                try {
                    String sql;
                    photoDatingArr = new JSONArray();
                    sql = "select p.uploaderUserId, p.photoId, p.photoPath \n" +
                            "from PHOTOSDATING p \n" +
                            "join USERDATING u on p.uploaderUserId = u.userId\n" +
                            "where p.uploaderUserId = (select userid from USERDATING where fname = ?)";
                    stm = con.prepareStatement(sql);
                    System.out.println("Inside try block");
                    stm.setString(1, theFName);
                    rs = stm.executeQuery();
                    while(rs.next())
                    {
                         mainObj = new JSONObject();
                         uploaderUserId = rs.getInt(1);
                         photoId = rs.getInt(2);
                         photoPath = rs.getString(3);
                        
                         mainObj.accumulate("photoId", photoId);
                         mainObj.accumulate("uploaderUserId", uploaderUserId);
                         mainObj.accumulate("photoPath", photoPath);
                         
                         photoDatingArr.add(mainObj);
                         System.out.println(mainObj);
                         System.out.println("Inside while");
                    }
                    listObj.accumulate("Status", "OK");
                    listObj.accumulate("Timestamp", timeStamp());
                    listObj.accumulate("Photos", photoDatingArr);
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
     * Add Photo - Adding a photo
     */
    @GET
    @Path("/addPhoto&{uploaderUserId}&{photoPath}")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPhoto(@PathParam("uploaderUserId") int theUploaderUserId,
                           @PathParam("photoPath") String thePhotoPath)
    {
        ResultSet rs = null;
        photoDating = new PhotoDating();
        
                try {
                    String sql;
                    // escape the forward slashes as %2F
                    sql = "insert into PHOTOSDATING(photoId, uploaderUserId, photoPath) values (photo_id_seq.nextval, ?, ?)";
                    stm = con.prepareStatement(sql);
                    stm.setInt(1, theUploaderUserId);
                    stm.setString(2, thePhotoPath);
                                       
                    int rs1 = stm.executeUpdate();
                    mainObj = new JSONObject();
                    if(rs1==1)
                    {
                        mainObj.accumulate("Status", "OK");
                        mainObj.accumulate("Timestamp", timeStamp());
                        mainObj.accumulate("message", "Photo added successfully");
                    }
                } catch (SQLException ex) {
                    mainObj.accumulate("Status", "FAIL");
                    mainObj.accumulate("Timestamp", timeStamp());
                    mainObj.accumulate("message", "Error adding photo");
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