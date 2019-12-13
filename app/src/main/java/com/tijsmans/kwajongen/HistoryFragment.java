package com.tijsmans.kwajongen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";

    private Game mGame;

    private RecyclerView mScoresRecyclerView;
    private ScoreAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID gameId = (UUID) getActivity().getIntent()
                .getSerializableExtra(HistoryActivity.EXTRA_GAME_ID);
        mGame = ScoreLab.get(getActivity()).getGame(gameId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        mScoresRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_history_recycler_view);
        mScoresRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        updateUI();

        return v;
    }

    private class ScoreHolder extends RecyclerView.ViewHolder {

        private Score mScore;
        private TextView mZijScore;
        private TextView mWijScore;

        public ScoreHolder(View itemView) {
            super(itemView);

            mZijScore = (TextView) itemView.findViewById(R.id.zij_score);
            mWijScore = (TextView) itemView.findViewById(R.id.wij_score);
        }

        public void bindScore(Score score) {
            mScore = score;
            if (mScore.isPassenspel()) {
                mZijScore.setText("-");
                mWijScore.setText("-");
            } else {
                mZijScore.setText(String.valueOf(mScore.getZijScore()));
                mWijScore.setText(String.valueOf(mScore.getWijScore()));
            }
        }
    }

    private class ScoreAdapter extends RecyclerView.Adapter<ScoreHolder> {

        private List<Score> mScores;
        private int mPosition;

        public ScoreAdapter(List<Score> scores) {
            mScores = scores;
        }

        @Override
        public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_score, parent, false);
            return new ScoreHolder(view);
        }

        @Override
        public void onBindViewHolder(ScoreHolder holder, int position) {
            Score score = mScores.get(position);
            holder.bindScore(score);
        }

        @Override
        public int getItemCount() {
            return mScores.size();
        }

        public void setScores(List<Score> scores) {
            mScores = scores;
        }
    }

    private void updateUI() {
        List<Score> scores = ScoreLab.get(getActivity()).getScoresOfGame(mGame);
        if (mAdapter == null) {
            mAdapter = new ScoreAdapter(scores);
            mScoresRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setScores(scores);
            mAdapter.notifyDataSetChanged();
        }
    }

}
