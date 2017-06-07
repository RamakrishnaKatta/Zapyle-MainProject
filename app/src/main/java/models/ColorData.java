package models;

/**
 * Created by haseeb on 23/1/16.
 */
public class ColorData {

    Integer id;
    String color;
    String colorCode;
    Boolean is_selected;


    public String getColor(){
        return color;
    }
    public void setColor(String color){
        this.color = color;
    }

    public String getColorCode(){
        return colorCode;
    }

    public void setColorCode(String colorCode){
        this.colorCode = colorCode;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Boolean getIs_selected(){
        return is_selected;
    }

    public void setIs_selected(Boolean is_selected){

    }
}
