package models;

/**
 * Created by haseeb on 28/7/15.
 */
public class LikeData {
    String likersname;
    String profilePic;
    Integer userid;
    Integer likeid;



    public Integer getUserid(){
        return userid;
    }
    public void setUserid(Integer userid){
        this.userid = userid;
    }

    public Integer getLikeid(){
        return likeid;
    }

    public void setLikeid(Integer likeid){
        this.likeid = likeid;
    }

    public String getLikersname() {

        return likersname;
    }

    public void setLikersname(String likersname) {

        this.likersname = likersname;
    }

    public String getprofilePic()
    {
        return profilePic;
    }

    public void setprofilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}
