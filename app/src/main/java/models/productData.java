package models;

/**
 * Created by zapyle on 6/12/16.
 */

public class productData {
    String _Imagev;
    String _Targetv;
    String productname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    String brandname;
    String lprice;
    String oprice;

    public String getproductname() {
        return productname;
    }

    public void setproductname(String name) {
        this.productname = name;
    }

    public String getLprice() {
        return lprice;
    }

    public void setLprice(String lprice) {
        this.lprice = lprice;
    }

    public String getOprice() {
        return oprice;
    }

    public void setOprice(String oprice) {
        this.oprice = oprice;
    }


    public String getImage() {
        return _Imagev;
    }
    public void setImage(String _Image) {
        _Imagev = _Image;
    }
    public String getTarget() {
        return _Targetv;
    }
    public void set_Target(String _Target) {
        _Targetv = _Target;
    }

}
