package models;

/**
 * Created by haseeb on 22/1/16.
 */
public class OccasionData {

    String occasion;
    Integer occasionId;

    public void setOccasion(String occasion){
        this.occasion = occasion;
    }
    public String getOccasion(){
        return occasion;
    }



    public void setOccasionId(Integer occasionId){
        this.occasionId = occasionId;
    }

    public Integer getOccasionId(){
        return occasionId;
    }
}
