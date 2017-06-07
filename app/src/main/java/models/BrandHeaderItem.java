package models;

import com.brandongogetap.stickyheaders.exposed.StickyHeader;

/**
 * Created by haseeb on 14/12/16.
 */

public class BrandHeaderItem extends category_list_model implements StickyHeader{
    String name, target, type;
    int index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
