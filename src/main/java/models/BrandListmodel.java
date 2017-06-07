package models;

/**
 * Created by haseeb on 15/1/16.
 */
public class BrandListmodel {

    String brandname;
    int brand_id;
    Boolean is_selected;

    public String getBrandname(){
        return  brandname;
    }

    public void setBrandname(String brandname){
        this.brandname = brandname;
    }

    public int getBrand_id(){
        return brand_id;
    }

    public void setBrand_id(int brand_id){
        this.brand_id = brand_id;
    }

    public Boolean getIs_selected(){
        return is_selected;
    }

    public void setIs_selected(Boolean is_selected){
        this.is_selected = is_selected;
    }
}
