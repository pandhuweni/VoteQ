package com.ceria.pkl.voteq.models.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by win 8 on 10/21/2016.
 */
public class CreateVoteResponse {
    @SerializedName("status")
    public String status;
    @SerializedName("data")
    public Vote data;
}
