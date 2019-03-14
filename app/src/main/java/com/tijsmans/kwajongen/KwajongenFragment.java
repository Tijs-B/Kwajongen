package com.tijsmans.kwajongen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;


public class KwajongenFragment extends Fragment {

    private static final String TAG = "KwajongenFragment";
    private static final String DIALOG_NEW_GAME = "DialogNewGame";

    private static final int REQUEST_NEW_GAME = 0;

    private Game mGame;
    private RecyclerView mScoreRecyclerView;
    private Button mPassenspel;
    private Button mOngedaanMaken;
    private Button mWijGewonnen;
    private CheckBox mWijKapot;
    private CheckBox mWijAangespeeld;
    private Button mZijGewonnen;
    private CheckBox mZijKapot;
    private CheckBox mZijAangespeeld;
    private KwajongenFragment.ScoreAdapter mAdapter;

    public static KwajongenFragment newInstance() {
        return new KwajongenFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mGame = ScoreLab.get(getActivity()).getLatestGame();
        if (mGame == null) {
            mGame = ScoreLab.get(getActivity()).newGame(10);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kwajongen, container, false);

        mScoreRecyclerView = (RecyclerView)
                view.findViewById(R.id.fragment_kwajongen_recycler_view);
        mScoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mWijKapot = (CheckBox) view.findViewById(R.id.wij_kapot);
        mWijAangespeeld = (CheckBox) view.findViewById(R.id.wij_aangespeeld);
        mZijKapot = (CheckBox) view.findViewById(R.id.zij_kapot);
        mZijAangespeeld = (CheckBox) view.findViewById(R.id.zij_aangespeeld);

        mPassenspel = (Button) view.findViewById(R.id.passenspel);
        mPassenspel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Score latestScore = ScoreLab.get(getActivity()).getLatestScore(mGame);
                Score newScore = new Score();
                newScore.setPassenspel(true);
                newScore.setWijScore(latestScore.getWijScore());
                newScore.setZijScore(latestScore.getZijScore());
                newScore.setGameId(mGame.getId());
                ScoreLab.get(getActivity()).addScore(newScore);
                updateUI();
            }
        });

        mOngedaanMaken = (Button) view.findViewById(R.id.ongedaan_maken);
        mOngedaanMaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreLab.get(getActivity()).deleteLatestScore(mGame);
                updateUI();
            }
        });

        mWijGewonnen = (Button) view.findViewById(R.id.wij_gewonnen);
        mWijGewonnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Score latestScore = ScoreLab.get(getActivity()).getLatestScore(mGame);
                Score newScore = new Score();
                int newWijScore = latestScore.getWijScore() - 1;
                int newZijScore = latestScore.getZijScore();
                if (mWijAangespeeld.isChecked())
                    newZijScore += 1;
                if (mWijKapot.isChecked())
                    newWijScore -= 1;
                if (latestScore.isPassenspel())
                    newWijScore -= 1;
                newScore.setWijScore(newWijScore);
                newScore.setZijScore(newZijScore);
                newScore.setGameId(mGame.getId());
                if (newScore.getWijScore() <= 0) {
                } else if (newScore.getZijScore() <= 0) {
                }
                ScoreLab.get(getActivity()).addScore(newScore);
                updateUI();
            }
        });

        mZijGewonnen = (Button) view.findViewById(R.id.zij_gewonnen);
        mZijGewonnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Score latestScore = ScoreLab.get(getActivity()).getLatestScore(mGame);
                Score newScore = new Score();
                int newWijScore = latestScore.getWijScore();
                int newZijScore = latestScore.getZijScore() - 1;
                if (mZijAangespeeld.isChecked())
                    newWijScore += 1;
                if (mZijKapot.isChecked())
                    newZijScore -= 1;
                if (latestScore.isPassenspel())
                    newZijScore -= 1;
                newScore.setWijScore(newWijScore);
                newScore.setZijScore(newZijScore);
                newScore.setGameId(mGame.getId());
                ScoreLab.get(getActivity()).addScore(newScore);
                updateUI();
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_NEW_GAME) {
            int startingPoints = data.getIntExtra(NewGameDialogFragment.EXTRA_STARTING_POINTS, 10);
            mGame = ScoreLab.get(getActivity()).newGame(startingPoints);
            updateUI();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_kwajongen_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_game:
                FragmentManager manager = getFragmentManager();
                NewGameDialogFragment dialog = NewGameDialogFragment.newInstance();
                dialog.setTargetFragment(KwajongenFragment.this, REQUEST_NEW_GAME);
                dialog.show(manager, DIALOG_NEW_GAME);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            mScoreRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setScores(scores);
            mAdapter.notifyDataSetChanged();
        }
        mWijKapot.setChecked(false);
        mWijAangespeeld.setChecked(false);
        mZijKapot.setChecked(false);
        mZijAangespeeld.setChecked(false);
        mScoreRecyclerView.scrollToPosition(ScoreLab.get(getActivity()).getNumberOfScores(mGame.getId()) - 1);
    }
}
