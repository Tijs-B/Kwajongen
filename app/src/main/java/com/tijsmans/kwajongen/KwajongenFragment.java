package com.tijsmans.kwajongen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

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
//    private KonfettiView mViewKonfetti;

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

//        mViewKonfetti = (KonfettiView) view.findViewById(R.id.viewKonfetti);

        mWijScore = (TextView) view.findViewById(R.id.wij_score);
        mWijKapot = (CheckBox) view.findViewById(R.id.wij_kapot);
        mWijAangespeeld = (CheckBox) view.findViewById(R.id.wij_aangespeeld);

        mZijScore = (TextView) view.findViewById(R.id.zij_score);
        mZijKapot = (CheckBox) view.findViewById(R.id.zij_kapot);
        mZijAangespeeld = (CheckBox) view.findViewById(R.id.zij_aangespeeld);

        View.OnClickListener wijGewonnenOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Score latestScore = ScoreLab.get(getActivity()).getLatestScore(mGame);
                Score newScore = latestScore.calculateNewScore(
                        "wij", mWijAangespeeld.isChecked(), mWijKapot.isChecked()
                );
                if (newScore != null) {
                    ScoreLab.get(getActivity()).addScore(newScore);
                    updateUI();
                } else {
                    openNewGameDialog();
                }
            }
        };

        mWijGewonnen = (Button) view.findViewById(R.id.wij_gewonnen);
        mWijGewonnen.setOnClickListener(wijGewonnenOnClickListener);
        mWijScore.setOnClickListener(wijGewonnenOnClickListener);

        View.OnClickListener zijOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Score latestScore = ScoreLab.get(getActivity()).getLatestScore(mGame);
                Score newScore = latestScore.calculateNewScore(
                        "zij", mZijAangespeeld.isChecked(), mZijKapot.isChecked()
                );
                if (newScore != null) {
                    ScoreLab.get(getActivity()).addScore(newScore);
                    updateUI();
                } else {
                    openNewGameDialog();
                }
            }
        };

        mZijGewonnen = (Button) view.findViewById(R.id.zij_gewonnen);
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
                openNewGameDialog();
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

    private void openNewGameDialog() {
        FragmentManager manager = getFragmentManager();
        NewGameDialogFragment dialog = NewGameDialogFragment.newInstance();
        dialog.setTargetFragment(KwajongenFragment.this, REQUEST_NEW_GAME);
        dialog.show(manager, DIALOG_NEW_GAME);
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

//        if (latest.isFinished()) {
//            mViewKonfetti.build()
//                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
//                    .setDirection(0.0, 359.0)
//                    .setSpeed(1f, 5f)
//                    .setFadeOutEnabled(true)
//                    .setTimeToLive(2000L)
//                    .addShapes(Shape.RECT, Shape.CIRCLE)
//                    .addSizes(new Size(12, 5))
//                    .setPosition(-50f, mViewKonfetti.getWidth() + 50f, -50f, -50f)
//                    .streamFor(300, 5000L);
//        }
    }
}
