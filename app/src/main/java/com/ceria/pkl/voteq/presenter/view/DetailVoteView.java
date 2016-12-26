package com.ceria.pkl.voteq.presenter.view;

import android.graphics.Path;
import android.util.Log;

import com.ceria.pkl.voteq.itemAdapter.OptionItem;
import com.ceria.pkl.voteq.itemAdapter.ResultItem;
import com.ceria.pkl.voteq.itemAdapter.VoteItem;
import com.ceria.pkl.voteq.models.network.ApiClient;
import com.ceria.pkl.voteq.models.network.DetailVoteClient;
import com.ceria.pkl.voteq.models.pojo.DetailVoteResponse;
import com.ceria.pkl.voteq.models.pojo.Option;
import com.ceria.pkl.voteq.models.pojo.Vote;
import com.ceria.pkl.voteq.models.pojo.VoteUser;
import com.ceria.pkl.voteq.presenter.callback.DetailVoteCallBack;
import com.ceria.pkl.voteq.presenter.viewinterface.VotingInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by win 8 on 10/21/2016.
 */
public class DetailVoteView implements DetailVoteCallBack {
    private List<OptionItem> optionItemList = new ArrayList<>();
    private VoteItem voteItem;
    private VotingInterface votingInterface;

    private String setDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        try {
            String dateText = String.valueOf(sdf.parse(date));
            Date dateFix = sdf1.parse(dateText);
            return String.valueOf(dateFix);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("error", e.toString());
            return date;
        }
    }

    public List<OptionItem> getOptionItemList() {
        return optionItemList;
    }

    public void setVoteItem(VoteItem voteItem) {
        this.voteItem = voteItem;
    }

    public VoteItem getVoteItem() {
        return voteItem;
    }

    public DetailVoteView(VotingInterface votingInterface) {
        this.votingInterface = votingInterface;
    }

    @Override
    public void callDetailVote(String id) {
        if (votingInterface != null) {
            votingInterface.showProgress();
        }

        final DetailVoteClient detailVoteClient = ApiClient.getClient().create(DetailVoteClient.class);
        Call<DetailVoteResponse> call = detailVoteClient.detailVote(id);
        call.enqueue(new Callback<DetailVoteResponse>() {
            @Override
            public void onResponse(Call<DetailVoteResponse> call, Response<DetailVoteResponse> response) {
                if (response.code() == 200) {
                  Vote vote = response.body().data;
                    String category;
                    try {
                        category = vote.category;
                    }catch (Exception e){
                        category = "Uncategorized";
                    }
                    VoteItem voteObject = new VoteItem(vote.id, vote.user.name, setDate(vote.started_at),
                            setDate(vote.ended_at), vote.title, category, vote.description,
                            vote.status, vote.participant, vote.user.image, vote.image);
                    setVoteItem(voteObject);
                    Option[] options = vote.options;
                        for (int i = 0; i < options.length; i++) {
                            String id = options[i].id;
                            String title = options[i].title;
                            String percentage = options[i].percentage;
                            int total_voter = options[i].total_voter;
                            String image = options[i].image;
                           optionItemList.add(new OptionItem(id, title, percentage, total_voter, image));
                        }

                    votingInterface.hideProgress();
                    votingInterface.onSucceededGetVote();
                } else {
                    Log.d("LOG", "response code : " + response.code());
                    votingInterface.hideProgress();
                    votingInterface.setCredentialError();
                }
            }

            @Override
            public void onFailure(Call<DetailVoteResponse> call, Throwable t) {
                votingInterface.hideProgress();
                votingInterface.onNetworkFailure();
            }
        });

    }

    @Override
    public void onDestroy() {
        votingInterface = null;
    }
}
