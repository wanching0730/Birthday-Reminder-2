package com.wanching.birthdayreminder;

/**
 * Created by WanChing on 8/8/2017.
 */

public class DrawerItem {

    String itemName;
    int imageId;

    public DrawerItem(String itemName, int imageId){
        super();
        this.itemName = itemName;
        this.imageId = imageId;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public int getImgResID() {
        return imageId;
    }
    public void setImgResID(int imageId) {
        this.imageId = imageId;
    }

}
