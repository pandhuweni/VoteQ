package com.ceria.pkl.voteq.models.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Haniyah on 12/24/2016.
 */

public class Category {
    @SerializedName("id")
    public String id;
    @SerializedName("category")
    public String category;

    public String getCategory(){
        return category;
    }
}