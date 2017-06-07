package models;

/**
 * Created by haseeb on 12/8/16.
 */
public class ProfileDetails {
    String usertype,
            username,
            description,
            profilepic,
            shortDescription,
            coverPic;

    int admiringCount,
            admirecount,
            lisingcount,
            userId;

    Boolean admiredBy;


    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setAdmirecount(int admirecount) {
        this.admirecount = admirecount;
    }

    public void setAdmiredBy(Boolean admiredBy) {
        this.admiredBy = admiredBy;
    }

    public void setAdmiringCount(int admiringCount) {
        this.admiringCount = admiringCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setLisingcount(int lisingcount) {
        this.lisingcount = lisingcount;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUsername() {
        return username;
    }

    public String getUsertype() {
        return usertype;
    }


    public Boolean getAdmiredBy() {
        return admiredBy;
    }

    public int getAdmirecount() {
        return admirecount;
    }

    public int getAdmiringCount() {
        return admiringCount;
    }

    public int getLisingcount() {
        return lisingcount;
    }

    public String getDescription() {
        return description;
    }

    public String getProfilepic() {
        return profilepic;
    }

}
