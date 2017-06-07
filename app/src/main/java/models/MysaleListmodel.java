package models;

/**
 * Created by zapyle on 27/1/16.
 */
public class MysaleListmodel {


    String strimagepath;
    String strprodname;
    String strsize;
    float stramount;
    Boolean boolreturn;

    public String getimagepath(){
        return  strimagepath;
    }
    public String getproductname(){
        return  strprodname;
    }
    public String getsize(){
        return  strsize;
    }
    public float getamount(){
        return  stramount;
    }
    public boolean getreturn(){
        return  boolreturn;
    }

    public void setimagepath(String imagepath){
        this.strimagepath = imagepath;
    }
    public void setproductname(String prodname){
        this.strprodname = prodname;
    }
    public void setsize(String size){
        this.strsize = size;
    }
    public void setamount(float amount){
        this.stramount = amount;
    }
    public void setreturn(boolean boolreturn){
        this.boolreturn =boolreturn;
    }
}
