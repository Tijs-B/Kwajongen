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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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
import java.util.Locale;


public class KwajongenFragment extends Fragment {

    private static final String TAG = "KwajongenFragment";
    private static final String DIALOG_NEW_GAME = "DialogNewGame";

    private static final int REQUEST_NEW_GAME = 0;

    private Game mGame;

    private TextView mWijScore;
    private Button mWijGewonnen;
    private CheckBox mWijKapot;
    private CheckBox mWijAangespeeld;

    private TextView mZijScore;
    private Button mZijGewonnen;
    private CheckBox mZijKapot;
    private CheckBox mZijAangespeeld;

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

        mWijScore = (TextView) view.findViewById(R.id.wij_score);
        mWijKapot = (CheckBox) view.findViewById(R.id.wij_kapot);
        mWijAangespeeld = (CheckBox) view.findViewById(R.id.wij_aangespeeld);

        mZijScore = (TextView) view.findViewById(R.id.zij_score);
        mZijKapot = (CheckBox) view.findViewById(R.id.zij_kapot);
        mZijAangespeeld = (CheckBox) view.findViewById(R.id.zij_aangespeeld);

        mWijGewonnen = (Button) view.findViewById(R.id.wij_gewonnen);
        View.OnClickListener wijGewonnenOnClickListener = new View.OnClickListener() {
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

                ScoreLab.get(getActivity()).addScore(newScore);

                updateUI();
            }
        };
        mWijGewonnen.setOnClickListener(wijGewonnenOnClickListener);
        mWijScore.setOnClickListener(wijGewonnenOnClickListener);

        mZijGewonnen = (Button) view.findViewById(R.id.zij_gewonnen);
        View.OnClickListener zijOnClickListener = new View.OnClickListener() {
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
        };
        mZijGewonnen.setOnClickListener(zijOnClickListener);
        mZijScore.setOnClickListener(zijOnClickListener);

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
            case R.id.menu_item_history:
                Intent intent = HistoryActivity.newIntent(getActivity(), mGame.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_passenspel:
                Score latestScore = ScoreLab.get(getActivity()).getLatestScore(mGame);

                if (latestScore.isPassenspel()) {
                    return true;
                }

                Score newScore = new Score();

                newScore.setPassenspel(true);
                newScore.setWijScore(latestScore.getWijScore());
                newScore.setZijScore(latestScore.getZijScore());
                newScore.setGameId(mGame.getId());

                ScoreLab.get(getActivity()).addScore(newScore);

                updateUI();
                return true;
            case R.id.menu_item_undo:
                ScoreLab.get(getActivity()).deleteLatestScore(mGame);
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void updateUI() {

        Score latest = ScoreLab.get(getActivity()).getLatestScore(mGame);

        Locale locale = getResources().getConfiguration().locale;
        String wijScoreString = String.format(locale, "%d", latest.getWijScore());
        String zijScoreString = String.format(locale, "%d", latest.getZijScore());

        if (latest.isPassenspel()) {
            SpannableString content = new SpannableString(wijScoreString);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mWijScore.setText(content);

            content = new SpannableString(zijScoreString);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mZijScore.setText(content);
        } else {
            mWijScore.setText(wijScoreString);
            mZijScore.setText(zijScoreString);
        }

        mWijKapot.setChecked(false);
        mWijAangespeeld.setChecked(false);
        mZijKapot.setChecked(false);
        mZijAangespeeld.setChecked(false);
//        mScoreRecyclerView.scrollToPosition(ScoreLab.get(getActivity()).getNumberOfScores(mGame.getId()) - 1);
    }
}
