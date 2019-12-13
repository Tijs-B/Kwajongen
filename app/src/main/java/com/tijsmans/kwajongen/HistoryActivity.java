package com.tijsmans.kwajongen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class HistoryActivity extends SingleFragmentActivity {

    public static final String EXTRA_GAME_ID = "com.tijsmans.kwajongen.game_id";

    public static Intent newIntent(Context packageContext, UUID game_id) {
        Intent intent = new Intent(packageContext, HistoryActivity.class);
        intent.putExtra(EXTRA_GAME_ID, game_id);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new HistoryFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
