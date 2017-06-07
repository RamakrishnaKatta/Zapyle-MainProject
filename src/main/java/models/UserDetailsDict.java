package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hitech on 9/7/15.
 */
public class UserDetailsDict {
    @Expose
    private String zap_username;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @Expose
    private Boolean verified;
    @Expose
    private String description;
    @SerializedName("last_join")
    @Expose
    private String lastJoin;
    @SerializedName("logged_from")
    @Expose
    private String loggedFrom;
    @Expose
    private String email;
    @SerializedName("cover_pic")
    @Expose
    private String coverPic;
    @Expose
    private Integer id;
    @Expose
    private String size;

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return zap_username;
    }

    /**
     *
     * @param zap_username
     * The username
     */
    public void setUsername(String zap_username) {
        this.zap_username = zap_username;
    }

    /**
     *
     * @return
     * The profilePic
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     *
     * @param profilePic
     * The profile_pic
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The verified
     */
    public Boolean getVerified() {
        return verified;
    }

    /**
     *
     * @param verified
     * The verified
     */
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The lastJoin
     */
    public String getLastJoin() {
        return lastJoin;
    }

    /**
     *
     * @param lastJoin
     * The last_join
     */
    public void setLastJoin(String lastJoin) {
        this.lastJoin = lastJoin;
    }

    /**
     *
     * @return
     * The loggedFrom
     */
    public String getLoggedFrom() {
        return loggedFrom;
    }

    /**
     *
     * @param loggedFrom
     * The logged_from
     */
    public void setLoggedFrom(String loggedFrom) {
        this.loggedFrom = loggedFrom;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The coverPic
     */
    public String getCoverPic() {
        return coverPic;
    }

    /**
     *
     * @param coverPic
     * The cover_pic
     */
    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The size
     */
    public String getSize() {
        return size;
    }

    /**
     *
     * @param size
     * The size
     */
    public void setSize(String size) {
        this.size = size;
    }
}
