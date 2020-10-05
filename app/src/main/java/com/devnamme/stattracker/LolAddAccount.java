package com.devnamme.stattracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LolAddAccount extends AppCompatActivity {
    private final byte BASIC_INFORMATION = 0;
    private final byte SOLO_DUO = 1;
    private final byte FLEX = 2;
    private final byte TEAMFIGHT_TACTICS = 3;
    private byte inFocus = BASIC_INFORMATION;

    private final byte SERIES_DASH = 0;
    private final byte SERIES_CHECK = 1;
    private final byte SERIES_CROSS = 2;


    private BottomSheetBehavior SoloDuoBottomSheetBehavior;
    private BottomSheetBehavior FlexBottomSheetBehavior;
    private BottomSheetBehavior TeamfightTacticsBottomSheetBehavior;

    ImageView icon_field = null;
    EditText username_field = null;
    EditText level_field = null;
    Spinner server_field = null;
    EditText club_field = null;
    TextView matchHistory_label = null;
    EditText matchHistory_field = null;

    ImageView SoloDuo_current_emblem = null;
    Spinner SoloDuo_current_emblemUpgrade = null;
    Spinner SoloDuo_current_today_tier = null;
    LinearLayout SoloDuo_current_today_division_container = null;
    Spinner SoloDuo_current_today_division = null;
    LinearLayout SoloDuo_current_today_lp_container = null;
    EditText SoloDuo_current_today_lp = null;
    LinearLayout SoloDuo_current_today_series_container = null;
    byte[] SoloDuo_current_today_series = { 0, 0, 0, 0, 0 };
    ImageView SoloDuo_current_today_series_1 = null;
    ImageView SoloDuo_current_today_series_2 = null;
    ImageView SoloDuo_current_today_series_3 = null;
    ImageView SoloDuo_current_today_series_4 = null;
    ImageView SoloDuo_current_today_series_5 = null;
    LinearLayout SoloDuo_current_data_container = null;
    LinearLayout SoloDuo_old_data_container = null;
    Button SoloDuo_old_data_add = null;
    byte[] SoloDuo_old_data_seasons = new byte[LeagueOfLegends.CURRENT_SEASON - 1];

    ImageView Flex_current_emblem = null;
    Spinner Flex_current_emblemUpgrade = null;
    Spinner Flex_current_today_tier = null;
    LinearLayout Flex_current_today_division_container = null;
    Spinner Flex_current_today_division = null;
    LinearLayout Flex_current_today_lp_container = null;
    EditText Flex_current_today_lp = null;
    LinearLayout Flex_current_today_series_container = null;
    byte[] Flex_current_today_series = { 0, 0, 0, 0, 0 };
    ImageView Flex_current_today_series_1 = null;
    ImageView Flex_current_today_series_2 = null;
    ImageView Flex_current_today_series_3 = null;
    ImageView Flex_current_today_series_4 = null;
    ImageView Flex_current_today_series_5 = null;
    LinearLayout Flex_old_data_container = null;
    Button Flex_old_data_add = null;
    byte[] Flex_old_data_seasons = new byte[LeagueOfLegends.CURRENT_SEASON - 1];

    ImageView TeamfightTactics_current_emblem = null;
    Spinner TeamfightTactics_current_emblemUpgrade = null;
    Spinner TeamfightTactics_current_today_tier = null;
    LinearLayout TeamfightTactics_current_today_division_container = null;
    Spinner TeamfightTactics_current_today_division = null;
    LinearLayout TeamfightTactics_current_today_lp_container = null;
    EditText TeamfightTactics_current_today_lp = null;
    LinearLayout TeamfightTactics_old_data_container = null;
    Button TeamfightTactics_old_data_add = null;
    byte[] TeamfightTactics_old_data_sets = new byte[TeamfightTactics.CURRENT_SET - 1];

    Button finishData = null;
    
    boolean isDownloadingProfileIcons = false;
    
    int accountIndex = -1;


    BottomSheetDialog SelectIconBottomSheetDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lol_add_account);

        Dev.log("LolAddAccount.onCreate");

        Intent intent = getIntent();
        accountIndex = intent.getIntExtra("accountIndex", -1);

        try {
            View SoloDuoBottomSheet = findViewById(R.id.add_account_lol_soloDuo_bottomSheet);
            View FlexBottomSheet = findViewById(R.id.add_account_lol_flex_bottomSheet);
            View TeamfightTacticsBottomSheet = findViewById(R.id.add_account_tft_bottomSheet);

            SoloDuoBottomSheetBehavior = BottomSheetBehavior.from(SoloDuoBottomSheet);
            FlexBottomSheetBehavior = BottomSheetBehavior.from(FlexBottomSheet);
            TeamfightTacticsBottomSheetBehavior = BottomSheetBehavior.from(TeamfightTacticsBottomSheet);

            FlexBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            SoloDuoBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int newState) {
                    Dev.log("LolAddAccount.SoloDuoBottomSheet.onStateChanged: " + Dev.BottomSheetStateToString(newState));
                    if(newState == BottomSheetBehavior.STATE_EXPANDED && inFocus <= SOLO_DUO) {
                        inFocus = SOLO_DUO;
                        FlexBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        FlexBottomSheetBehavior.setHideable(false);
                    } else if(newState == BottomSheetBehavior.STATE_COLLAPSED && inFocus >= SOLO_DUO) {
                        inFocus = BASIC_INFORMATION;
                        FlexBottomSheetBehavior.setHideable(true);
                        FlexBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        TeamfightTacticsBottomSheetBehavior.setHideable(true);
                        TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {}
            });

            FlexBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int newState) {
                    Dev.log("LolAddAccount.FlexBottomSheet.onStateChanged: " + Dev.BottomSheetStateToString(newState));
                    if (newState == BottomSheetBehavior.STATE_EXPANDED && inFocus <= FLEX) {
                        inFocus = FLEX;
                        TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        TeamfightTacticsBottomSheetBehavior.setHideable(false);
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED && inFocus >= FLEX) {
                        inFocus = SOLO_DUO;
                        TeamfightTacticsBottomSheetBehavior.setHideable(true);
                        TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {}
            });

            TeamfightTacticsBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int newState) {
                    Dev.log("LolAddAccount.TeamfightTacticsBottomSheet.onStateChanged: " + Dev.BottomSheetStateToString(newState));
                    if (newState == BottomSheetBehavior.STATE_EXPANDED && inFocus <= TEAMFIGHT_TACTICS)
                        inFocus = TEAMFIGHT_TACTICS;
                    else if(newState == BottomSheetBehavior.STATE_COLLAPSED && inFocus >= TEAMFIGHT_TACTICS)
                        inFocus = FLEX;
                }

                @Override
                public void onSlide(@NonNull View view, float v) {}
            });

            /*
             * BASIC INFORMATION
             */
            icon_field = findViewById(R.id.add_account_lol_icon_field);
            username_field = findViewById(R.id.add_account_lol_username_field);
            level_field = findViewById(R.id.add_account_lol_level_field);
            server_field = findViewById(R.id.add_account_lol_server_spinner);
            club_field = findViewById(R.id.add_account_lol_club_field);
            matchHistory_label = findViewById(R.id.add_account_lol_match_history_label);
            matchHistory_field = findViewById(R.id.add_account_lol_match_history_field);

            finishData = findViewById(R.id.add_account_lol_finish);

            icon_field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check if profile icon exists
                    File profileIcons = new File(LolAddAccount.this.getExternalFilesDir(null).getPath() + "/assets/lol/profileicon0.png");
                    if(!profileIcons.exists()) {
                        // dialog to download
                        new AlertDialog.Builder(LolAddAccount.this)
                                .setTitle("Profile Icons")
                                .setMessage("Would you like to download all profile icons? This would take approximately 10MB.\n\nVersion: " + LeagueOfLegends.PATCH_NUMBER)
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(LolAddAccount.this, "Downloading all profile icons, please wait...", Toast.LENGTH_SHORT).show();
                                        isDownloadingProfileIcons = true;
                                        new Util.Download(LolAddAccount.this, LeagueOfLegends.ddAssets + "/" + LeagueOfLegends.ddLink + "/" + LeagueOfLegends.profileIconSpriteUrl, "assets/lol", "profileicon0.png", new Runnable() {
                                            @Override
                                            public void run() {
                                                isDownloadingProfileIcons = false;
                                                Toast.makeText(LolAddAccount.this, "Finished downloading all profile icons.", Toast.LENGTH_SHORT).show();
                                                showIconSelectDialog();
                                            }
                                        }).execute();
                                    }
                                }).create().show();
                    } else if(isDownloadingProfileIcons) {
                        // still downloading
                        Toast.makeText(LolAddAccount.this, "Download is not yet finished!", Toast.LENGTH_SHORT).show();
                    } else {
                        // show dialog
                        showIconSelectDialog();
                    }
                }
            });

            username_field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) { update_username(); }
            });

            level_field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) { update_level(); }
            });

            server_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position == Server.SEA.PH) {
                        matchHistory_label.setVisibility(View.VISIBLE);
                        matchHistory_field.setVisibility(View.VISIBLE);
                    } else {
                        matchHistory_label.setVisibility(View.GONE);
                        matchHistory_field.setVisibility(View.GONE);
                        matchHistory_field.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });


            /*
            * SOLO DUO CURRENT
             */
            SoloDuo_current_emblem = findViewById(R.id.add_account_lol_soloDuo_emblem);
            SoloDuo_current_emblemUpgrade = findViewById(R.id.add_account_lol_soloDuo_emblemUpgrade_spinner);
            SoloDuo_current_today_tier = findViewById(R.id.add_account_lol_soloDuo_current_today_tier_spinner);
            SoloDuo_current_today_division_container = findViewById(R.id.add_account_lol_soloDuo_current_today_division_container);
            SoloDuo_current_today_division = findViewById(R.id.add_account_lol_soloDuo_current_today_division_spinner);
            SoloDuo_current_today_lp_container = findViewById(R.id.add_account_lol_soloDuo_current_today_lp_container);
            SoloDuo_current_today_lp = findViewById(R.id.add_account_lol_soloDuo_current_today_lp_field);
            SoloDuo_current_today_series_container = findViewById(R.id.add_account_lol_soloDuo_current_today_series_container);
            SoloDuo_current_today_series_1 = findViewById(R.id.add_account_lol_soloDuo_current_today_series_1);
            SoloDuo_current_today_series_2 = findViewById(R.id.add_account_lol_soloDuo_current_today_series_2);
            SoloDuo_current_today_series_3 = findViewById(R.id.add_account_lol_soloDuo_current_today_series_3);
            SoloDuo_current_today_series_4 = findViewById(R.id.add_account_lol_soloDuo_current_today_series_4);
            SoloDuo_current_today_series_5 = findViewById(R.id.add_account_lol_soloDuo_current_today_series_5);
            SoloDuo_current_data_container = findViewById(R.id.add_account_lol_soloDuo_current_data_container);

            /*
            * SOLO DUO PREVIOUS
             */
            SoloDuo_old_data_container = findViewById(R.id.add_account_lol_soloDuo_old_data_container);
            SoloDuo_old_data_add = findViewById(R.id.add_account_lol_soloDuo_old_data_add);
            Arrays.fill(SoloDuo_old_data_seasons, (byte) 0);

            /*
            * FLEX CURRENT
             */
            Flex_current_emblem = findViewById(R.id.add_account_lol_flex_emblem);
            Flex_current_emblemUpgrade = findViewById(R.id.add_account_lol_flex_emblemUpgrade_spinner);
            Flex_current_today_tier = findViewById(R.id.add_account_lol_flex_current_today_tier_spinner);
            Flex_current_today_division_container = findViewById(R.id.add_account_lol_flex_current_today_division_container);
            Flex_current_today_division = findViewById(R.id.add_account_lol_flex_current_today_division_spinner);
            Flex_current_today_lp_container = findViewById(R.id.add_account_lol_flex_current_today_lp_container);
            Flex_current_today_lp = findViewById(R.id.add_account_lol_flex_current_today_lp_field);
            Flex_current_today_series_container = findViewById(R.id.add_account_lol_flex_current_today_series_container);
            Flex_current_today_series_1 = findViewById(R.id.add_account_lol_flex_current_today_series_1);
            Flex_current_today_series_2 = findViewById(R.id.add_account_lol_flex_current_today_series_2);
            Flex_current_today_series_3 = findViewById(R.id.add_account_lol_flex_current_today_series_3);
            Flex_current_today_series_4 = findViewById(R.id.add_account_lol_flex_current_today_series_4);
            Flex_current_today_series_5 = findViewById(R.id.add_account_lol_flex_current_today_series_5);

            /*
            * FLEX PREVIOUS
             */
            Flex_old_data_container = findViewById(R.id.add_account_lol_flex_old_data_container);
            Flex_old_data_add = findViewById(R.id.add_account_lol_flex_old_data_add);
            Arrays.fill(Flex_old_data_seasons, (byte) 0);

            /*
            * TFT CURRENT
             */
            TeamfightTactics_current_emblem = findViewById(R.id.add_account_lol_teamfightTactics_emblem);
            TeamfightTactics_current_emblemUpgrade = findViewById(R.id.add_account_lol_teamfightTactics_emblemUpgrade_spinner);
            TeamfightTactics_current_today_tier = findViewById(R.id.add_account_lol_teamfightTactics_current_today_tier_spinner);
            TeamfightTactics_current_today_division_container = findViewById(R.id.add_account_lol_teamfightTactics_current_today_division_container);
            TeamfightTactics_current_today_division = findViewById(R.id.add_account_lol_teamfightTactics_current_today_division_spinner);
            TeamfightTactics_current_today_lp_container = findViewById(R.id.add_account_lol_teamfightTactics_current_today_lp_container);
            TeamfightTactics_current_today_lp = findViewById(R.id.add_account_lol_teamfightTactics_current_today_lp_field);

            /*
            * TFT PREVIOUS
             */
            TeamfightTactics_old_data_container = findViewById(R.id.add_account_lol_teamfightTactics_old_data_container);
            TeamfightTactics_old_data_add = findViewById(R.id.add_account_lol_teamfightTactics_old_data_add);
            Arrays.fill(TeamfightTactics_old_data_sets, (byte) 0);


            if(accountIndex >= 0) useAccountData(LeagueOfLegends.accounts[accountIndex]);


            startUp(
                    SOLO_DUO,
                    SoloDuo_current_emblemUpgrade,
                    SoloDuo_current_today_tier,
                    SoloDuo_current_today_division_container,
                    SoloDuo_current_today_division,
                    SoloDuo_current_today_lp_container,
                    SoloDuo_current_today_lp,
                    SoloDuo_current_today_series_container,
                    new ImageView[] {
                            SoloDuo_current_today_series_1,
                            SoloDuo_current_today_series_2,
                            SoloDuo_current_today_series_3,
                            SoloDuo_current_today_series_4,
                            SoloDuo_current_today_series_5
                    }
            );

            startUp(
                    FLEX,
                    Flex_current_emblemUpgrade,
                    Flex_current_today_tier,
                    Flex_current_today_division_container,
                    Flex_current_today_division,
                    Flex_current_today_lp_container,
                    Flex_current_today_lp,
                    Flex_current_today_series_container,
                    new ImageView[] {
                            Flex_current_today_series_1,
                            Flex_current_today_series_2,
                            Flex_current_today_series_3,
                            Flex_current_today_series_4,
                            Flex_current_today_series_5
                    }
            );

            startUp(
                    TEAMFIGHT_TACTICS,
                    TeamfightTactics_current_emblemUpgrade,
                    TeamfightTactics_current_today_tier,
                    TeamfightTactics_current_today_division_container,
                    TeamfightTactics_current_today_division,
                    TeamfightTactics_current_today_lp_container,
                    TeamfightTactics_current_today_lp,
                    null,
                    null
            );

//            Dev.log("finished startup");
        } catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Dev.log("LolAddAccount.onBackPressed inFocus#" + inFocus);

            if(inFocus == TEAMFIGHT_TACTICS)
                FlexOnClick(null);
            else if(inFocus == FLEX)
                SoloDuoOnClick(null);
            else if(inFocus == SOLO_DUO)
                BasicInformationOnClick(null);
            else {
                new AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit? Changes will not be saved.")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dev.log("exited .LolAddAccount");

                                Intent intent = new Intent();
                                setResult(RESULT_CANCELED, intent);

                                finish();
                            }
                        }).create().show();
            }
        } catch(Exception e) { Dev.log("LolAddAccount.onBackPressed", e); }
    }

    public void BasicInformationOnClick(View view) {
//        try {
            Dev.log("LolAddAccount.BasicInformationOnClick");
            if (inFocus != BASIC_INFORMATION) {
                inFocus = BASIC_INFORMATION;
                SoloDuoBottomSheetBehavior.setHideable(false);
                SoloDuoBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                FlexBottomSheetBehavior.setHideable(true);
                FlexBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                TeamfightTacticsBottomSheetBehavior.setHideable(true);
                TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
//        } catch(Exception e) { Dev.log("LolAddAccount.BasicInformationOnClick", e); }
    }
    public void SoloDuoOnClick(View view) {
//        try {
            Dev.log("LolAddAccount.SoloDuoOnClick");
            if (inFocus != SOLO_DUO) {
                if(inFocus > SOLO_DUO) {
                    FlexBottomSheetBehavior.setHideable(false);
                    FlexBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    TeamfightTacticsBottomSheetBehavior.setHideable(true);
                    TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                SoloDuoBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
//        } catch(Exception e) { Dev.log("LolAddAccount.SoloDuoOnClick", e); }
    }
    public void FlexOnClick(View view) {
//        try {
            Dev.log("LolAddAccount.FlexOnClick");
            if (inFocus != FLEX) {
                if(inFocus > FLEX) {
                    TeamfightTacticsBottomSheetBehavior.setHideable(false);
                    TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                FlexBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
//        } catch(Exception e) { Dev.log("LolAddAccount.FlexOnClick", e); }
    }
    public void TeamfightTacticsOnClick(View view) {
//        try {
            Dev.log("LolAddAccount.TeamfightTacticsOnClick");
            if (inFocus != TEAMFIGHT_TACTICS)
                TeamfightTacticsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        } catch(Exception e) { Dev.log("LolAddAccount.TeamfightTacticsOnClick", e); }
    }



    protected void AddOldData(final byte mode, final LinearLayout oldDataContainer, final Button addOldDataButton) {
        try {
            final View container = LayoutInflater.from(this).inflate(R.layout.lol_add_account_old_data, oldDataContainer, false);
            container.setId(10000 + oldDataContainer.getChildCount());

            ImageButton removeData = container.findViewById(R.id.remove_data);
            removeData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Dev.log("removed id#" + container.getId());

                    ((ViewGroup) container.getParent()).removeView(container);
                    addOldDataButton.setVisibility(View.VISIBLE);

                    if(mode == SOLO_DUO) update_soloDuo();
                    else if(mode == FLEX) update_flex();
                    else if(mode == TEAMFIGHT_TACTICS) update_teamfightTactics();
                }
            });

            final Spinner season = container.findViewById(R.id.season_spinner);
            final Spinner tier = container.findViewById(R.id.tier_spinner);
            final Spinner division = container.findViewById(R.id.division_spinner);
            final Spinner emblemUpgrades = container.findViewById(R.id.emblemUpgrade_spinner);

            ArrayList<String> cur_season = new ArrayList<>();
            if(mode == SOLO_DUO || mode == FLEX) for(int i = 1; i < LeagueOfLegends.CURRENT_SEASON; i++) cur_season.add("S" + i);
            else for(int i = 1; i < TeamfightTactics.CURRENT_SET; i++) cur_season.add("S" + i);
            ArrayAdapter<String> season_adapter = new ArrayAdapter<>(this, R.layout.lol_add_account_spinner_small_item, cur_season);
            season_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
            season.setAdapter(season_adapter);

            ArrayAdapter<String> eu_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.emblem_upgrades));
            eu_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
            emblemUpgrades.setAdapter(eu_adapter);

            season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Dev.log("AddLolAccount." + mode + ".oldData.season.onItemSelected");

                    if(mode == SOLO_DUO) update_soloDuo();
                    else if(mode == FLEX) update_flex();
                    else update_teamfightTactics();

                    if(mode == SOLO_DUO) SoloDuo_old_data_seasons[position]++;
                    else if(mode == FLEX) Flex_old_data_seasons[position]++;
                    else TeamfightTactics_old_data_sets[position]++;

                    if(mode == SOLO_DUO || mode == FLEX) {
                        if(position < 8) {
                            // tier set to old
                            tier.setSelection(0);
                            ArrayAdapter<String> tier_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.rank_list_lol_old));
                            tier_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
                            tier.setAdapter(tier_adapter);
                            // division set to old
                            division.setSelection(0);
                            ArrayAdapter<String> division_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.division_list_lol_old));
                            division_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
                            division.setAdapter(division_adapter);
                            // emblemUpgrades => invisible
                            emblemUpgrades.setSelection(0);
                            emblemUpgrades.setVisibility(View.INVISIBLE);
                        } else {
                            tier.setSelection(0);
                            ArrayAdapter<String> tier_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.rank_list_lol));
                            tier_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
                            tier.setAdapter(tier_adapter);
                            division.setSelection(0);
                            ArrayAdapter<String> division_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.division_list_lol));
                            division_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
                            division.setAdapter(division_adapter);
                            emblemUpgrades.setSelection(0);
                            emblemUpgrades.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tier.setSelection(0);
                        ArrayAdapter<String> tier_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.rank_list_lol));
                        tier_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
                        tier.setAdapter(tier_adapter);
                        division.setSelection(0);
                        ArrayAdapter<String> division_adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lol_add_account_spinner_small_item, getResources().getStringArray(R.array.division_list_lol));
                        division_adapter.setDropDownViewResource(R.layout.lol_add_account_spinner_dropdown_item);
                        division.setAdapter(division_adapter);
                        emblemUpgrades.setSelection(0);
                        emblemUpgrades.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            tier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Dev.log("LolAddAccount." + mode + ".oldData.tier.onItemSelected");

                    if(mode == SOLO_DUO || mode == FLEX) {
                        if(season.getSelectedItemPosition() < 8) {
                            // translate
                            if(position > LeagueOfLegends.Rank.TIER_UNRANKED) position++;
                            if(position > LeagueOfLegends.Rank.TIER_MASTER) position++;
                        }
                    }

                    if(position == LeagueOfLegends.Rank.TIER_UNRANKED || position == LeagueOfLegends.Rank.TIER_MASTER || position == LeagueOfLegends.Rank.TIER_GRANDMASTER || position == LeagueOfLegends.Rank.TIER_CHALLENGER) {
                        // division => invisible
                        division.setSelection(0);
                        division.setVisibility(View.INVISIBLE);
                    } else {
                        if(division.getVisibility() == View.INVISIBLE)
                            division.setSelection(0);
                        division.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            oldDataContainer.addView(container);


            if(mode == SOLO_DUO || mode == FLEX) {
                if(oldDataContainer.getChildCount() == LeagueOfLegends.CURRENT_SEASON - 1)
                    addOldDataButton.setVisibility(View.GONE);
            } else if(oldDataContainer.getChildCount() == TeamfightTactics.CURRENT_SET - 1)
                addOldDataButton.setVisibility(View.GONE);
        } catch(Exception e) { Dev.log("LolAddAccount.AddOldData", e); }
    }
    public void SoloDuoAddOldData(View view) {
        Dev.log("LolAddAccount.SoloDuoAddOldData");

        AddOldData(SOLO_DUO, SoloDuo_old_data_container, SoloDuo_old_data_add);
    }
    public void FlexAddOldData(View view) {
        Dev.log("LolAddAccount.FlexAddOldData");

        AddOldData(FLEX, Flex_old_data_container, Flex_old_data_add);
    }
    public void TeamfightTacticsAddOldData(View view) {
        Dev.log("LolAddAccount.TeamfightTacticsAddOldData");

        AddOldData(TEAMFIGHT_TACTICS, TeamfightTactics_old_data_container, TeamfightTactics_old_data_add);
    }



    protected void drawEmblem(ImageView emblem, byte emblemUpgrade, LeagueOfLegends.Rank rank) {
        try {
            Dev.log("drawing emblem");

            BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(rank.getEmblemUpgrade(emblemUpgrade), null);
            int width = bitmapDrawable.getBitmap().getWidth();
            int height = bitmapDrawable.getBitmap().getHeight();
            int inPX = Util.convertDpToPx(this, 128);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    inPX * width / height,
                    inPX
            );
            int marginside = (inPX - (inPX * width / height)) / 2;
            layoutParams.setMargins(
                    marginside,
                    0,
                    marginside,
                    0
            );
            emblem.setLayoutParams(layoutParams);

            if (LeagueOfLegends.Rank.backgroundUpgradeNeeded(rank.tier, emblemUpgrade)) {
                emblem.setImageResource(rank.getEmblem());
                emblem.setBackgroundResource(rank.getEmblemUpgrade(emblemUpgrade));
            } else {
                emblem.setImageResource(rank.getEmblemUpgrade(emblemUpgrade));
                emblem.setBackgroundResource(0);
            }
        } catch(Exception e) { Dev.log("LolAddAccount.drawEmblem", e); }
    }
    protected void drawEmblem_SoloDuo() {
        LeagueOfLegends.Rank newRank = new LeagueOfLegends.Rank((byte) SoloDuo_current_today_tier.getSelectedItemPosition(), (byte) (SoloDuo_current_today_division.getSelectedItemPosition() + 1));
        drawEmblem(SoloDuo_current_emblem, (byte) SoloDuo_current_emblemUpgrade.getSelectedItemPosition(), newRank);
    }
    protected void drawEmblem_Flex() {
        LeagueOfLegends.Rank newRank = new LeagueOfLegends.Rank((byte) Flex_current_today_tier.getSelectedItemPosition(), (byte) (Flex_current_today_division.getSelectedItemPosition() + 1));
        drawEmblem(Flex_current_emblem, (byte) Flex_current_emblemUpgrade.getSelectedItemPosition(), newRank);
    }
    protected void drawEmblem_TeamfightTactics() {
        LeagueOfLegends.Rank newRank = new LeagueOfLegends.Rank((byte) TeamfightTactics_current_today_tier.getSelectedItemPosition(), (byte) (TeamfightTactics_current_today_division.getSelectedItemPosition() + 1));
        drawEmblem(TeamfightTactics_current_emblem, (byte) TeamfightTactics_current_emblemUpgrade.getSelectedItemPosition(), newRank);
    }



    protected void useAccountData(LeagueOfLegends.Account accountData) {
        try {
            Dev.log("using account " + accountData.username);

            username_field.setText(accountData.username);
            level_field.setText(Short.toString(accountData.level));
            server_field.setSelection(accountData.server);
            club_field.setText(accountData.club);
            matchHistory_field.setText(accountData.matchHistory);

            SoloDuo_current_emblemUpgrade.setSelection(accountData.emblemUpgrades);
            Flex_current_emblemUpgrade.setSelection(accountData.emblemUpgrades);
            TeamfightTactics_current_emblemUpgrade.setSelection(accountData.emblemUpgrades);

            LeagueOfLegends.Rank S_rank = accountData.LeagueOfLegends.SoloDuo.getCurrentRank();

            SoloDuo_current_today_tier.setSelection(S_rank.tier);
            SoloDuo_current_today_division.setSelection(S_rank.division - 1);
            SoloDuo_current_today_lp.setText(Short.toString(S_rank.lp));
            SoloDuo_current_today_series = S_rank.promos;
            updateImageSeries(SoloDuo_current_today_series[0], SoloDuo_current_today_series_1);
            updateImageSeries(SoloDuo_current_today_series[1], SoloDuo_current_today_series_2);
            updateImageSeries(SoloDuo_current_today_series[2], SoloDuo_current_today_series_3);
            updateImageSeries(SoloDuo_current_today_series[3], SoloDuo_current_today_series_4);
            updateImageSeries(SoloDuo_current_today_series[4], SoloDuo_current_today_series_5);

            LeagueOfLegends.Rank F_rank = accountData.LeagueOfLegends.Flex.getCurrentRank();

            Flex_current_today_tier.setSelection(F_rank.tier);
            Flex_current_today_division.setSelection(F_rank.division - 1);
            Flex_current_today_lp.setText(Short.toString(F_rank.lp));
            Flex_current_today_series = F_rank.promos;
            updateImageSeries(Flex_current_today_series[0], Flex_current_today_series_1);
            updateImageSeries(Flex_current_today_series[1], Flex_current_today_series_2);
            updateImageSeries(Flex_current_today_series[2], Flex_current_today_series_3);
            updateImageSeries(Flex_current_today_series[3], Flex_current_today_series_4);
            updateImageSeries(Flex_current_today_series[4], Flex_current_today_series_5);

            LeagueOfLegends.Rank T_rank = accountData.TeamfightTactics.getCurrentRank();

            TeamfightTactics_current_today_tier.setSelection(T_rank.tier);
            TeamfightTactics_current_today_division.setSelection(T_rank.division - 1);
            TeamfightTactics_current_today_lp.setText(Short.toString(T_rank.lp));


            finishData.setText(R.string.save_changes);
        } catch(Exception e) { Dev.log("LolAddAccount.useAccountData", e); }
    }
    protected void startUp(final byte _id, Spinner emblemUpgrade, final Spinner tier, final LinearLayout divisionContainer, final Spinner division, final LinearLayout lpContainer, final EditText lp, final LinearLayout seriesContainer, final ImageView[] seriesButton) {
        emblemUpgrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawEmblem_SoloDuo();
                drawEmblem_Flex();
                drawEmblem_TeamfightTactics();

                if(_id != SOLO_DUO)
                    SoloDuo_current_emblemUpgrade.setSelection(position);
                if(_id != FLEX)
                    Flex_current_emblemUpgrade.setSelection(position);
                if(_id != TEAMFIGHT_TACTICS)
                    TeamfightTactics_current_emblemUpgrade.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        tier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == (int) LeagueOfLegends.Rank.TIER_UNRANKED) {
                    divisionContainer.setVisibility(View.GONE);
                    division.setSelection(0);
                    lpContainer.setVisibility(View.GONE);
                    lp.setText(R.string.default_zero);
                    if(_id != TEAMFIGHT_TACTICS) {
                        seriesContainer.setVisibility(View.GONE);
                        flushSeries(_id, seriesButton);
                    }
                } else {
                    lpContainer.setVisibility(View.VISIBLE);
                    if(position != (int) LeagueOfLegends.Rank.TIER_MASTER && position != (int) LeagueOfLegends.Rank.TIER_GRANDMASTER && position != (int) LeagueOfLegends.Rank.TIER_CHALLENGER) {
                        divisionContainer.setVisibility(View.VISIBLE);
                    } else {
                        divisionContainer.setVisibility(View.GONE);
                        division.setSelection(0);
                    }

                    updateLP(_id, (byte) position, (byte) division.getSelectedItemPosition(), lp, seriesContainer, seriesButton);
                }

                if(_id == SOLO_DUO)
                    drawEmblem_SoloDuo();
                else if(_id == FLEX)
                    drawEmblem_Flex();
                else if(_id == TEAMFIGHT_TACTICS)
                    drawEmblem_TeamfightTactics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (_id == SOLO_DUO)
                    drawEmblem_SoloDuo();
                else if (_id == FLEX)
                    drawEmblem_Flex();
                else if (_id == TEAMFIGHT_TACTICS)
                    drawEmblem_TeamfightTactics();

                updateLP(_id, (byte) tier.getSelectedItemPosition(), (byte) position, lp, seriesContainer, seriesButton);

//                if (tier.getSelectedItemPosition() != (int) LeagueOfLegends.Rank.TIER_MASTER && tier.getSelectedItemPosition() != (int) LeagueOfLegends.Rank.TIER_GRANDMASTER && tier.getSelectedItemPosition() != (int) LeagueOfLegends.Rank.TIER_CHALLENGER) {
//                    if (_id != TEAMFIGHT_TACTICS && Integer.parseInt(lp.getText().toString()) == 100) {
//                        seriesContainer.setVisibility(View.VISIBLE);
//                        flushSeries(_id, seriesButton);
//                        if (position == 0) {
//                            seriesButton[3].setVisibility(View.VISIBLE);
//                            seriesButton[4].setVisibility(View.VISIBLE);
//                        } else {
//                            seriesButton[3].setVisibility(View.GONE);
//                            seriesButton[4].setVisibility(View.GONE);
//                        }
//                    }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        lp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) { updateLP(_id, (byte) tier.getSelectedItemPosition(), (byte) division.getSelectedItemPosition(), lp, seriesContainer, seriesButton); }
        });

        if(_id != TEAMFIGHT_TACTICS) {
            for (int i = 0; i < seriesButton.length; i++) {
                final ImageView img = seriesButton[i];
                img.setClickable(true);
                final int finalI = i;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            byte[] series;

                            boolean hasFoundPrev = false;

                            if (_id == SOLO_DUO) series = SoloDuo_current_today_series;
                            else series = Flex_current_today_series;

                            for (int ii = 0; ii < finalI; ii++) {
                                if (series[ii] == SERIES_DASH) {
                                    series[ii] = SERIES_CHECK;

                                    seriesButton[ii].setImageResource(R.drawable.circle_check);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        seriesButton[ii].setImageTintList(ColorStateList.valueOf(getColor(R.color.series_check)));
                                    else
                                        seriesButton[ii].setImageTintList(ColorStateList.valueOf(0xff50c878));

                                    hasFoundPrev = true;
                                    break;
                                }
                            }

                            if (!hasFoundPrev) {
                                if (series[finalI] == SERIES_DASH) {
                                    img.setImageResource(R.drawable.circle_check);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_check)));
                                    else
                                        img.setImageTintList(ColorStateList.valueOf(0xff50c878));
                                } else if (series[finalI] == SERIES_CHECK) {
                                    img.setImageResource(R.drawable.circle_x);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_x)));
                                    else
                                        img.setImageTintList(ColorStateList.valueOf(0xffff0800));
                                } else if (series[finalI] == SERIES_CROSS) {
                                    img.setImageResource(R.drawable.circle_dash);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_dash)));
                                    else
                                        img.setImageTintList(ColorStateList.valueOf(0xff5eb1bf));

                                    // check if next are ticked
                                    for (int ii = series.length - 1; ii > finalI; ii--) {
                                        if (series[ii] != SERIES_DASH) {
                                            seriesButton[ii].setImageResource(R.drawable.circle_dash);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                seriesButton[ii].setImageTintList(ColorStateList.valueOf(getColor(R.color.series_dash)));
                                            else
                                                seriesButton[ii].setImageTintList(ColorStateList.valueOf(0xff5eb1bf));

                                            series[ii] = SERIES_DASH;
                                        }
                                    }
                                }

                                series[finalI]++;
                            }

                            if (series[finalI] > SERIES_CROSS) series[finalI] = SERIES_DASH;
                            if (_id == SOLO_DUO) SoloDuo_current_today_series = series;
                            else if (_id == FLEX) Flex_current_today_series = series;
                        } catch(Exception e) { Dev.log("LolAddAccount.startUp.seriesButton.onClick", e); }
                    }
                });
            }
        }
    }

    protected void updateImageSeries(byte mode, ImageView img) {
        if(mode == SERIES_DASH) {
            img.setImageResource(R.drawable.circle_dash);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_dash)));
            else
                img.setImageTintList(ColorStateList.valueOf(0xff5eb1bf));
        } else if(mode == SERIES_CHECK) {
            img.setImageResource(R.drawable.circle_check);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_check)));
            else
                img.setImageTintList(ColorStateList.valueOf(0xff50c878));
        } else if(mode == SERIES_CROSS) {
            img.setImageResource(R.drawable.circle_x);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_x)));
            else
                img.setImageTintList(ColorStateList.valueOf(0xffff0800));
        }
    }
    protected void flushSeries(byte mode, final ImageView[] seriesButton) {
//        try {
            for(ImageView img : seriesButton) {
                img.setImageResource(R.drawable.circle_dash);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    img.setImageTintList(ColorStateList.valueOf(getColor(R.color.series_dash)));
                else
                    img.setImageTintList(ColorStateList.valueOf(0xff5eb1bf));
            }

            if(mode == SOLO_DUO)
                Arrays.fill(SoloDuo_current_today_series, (byte) 0);
            if(mode == FLEX)
                Arrays.fill(Flex_current_today_series, (byte) 0);
//        } catch(Exception e) { Dev.log("LolAddAccount.flushSeries", e); }
    }



    public void addAccount(View view) {
        Dev.log("LolAddAccount.addAccount");

        if(update_username() || update_level() || update_soloDuo() || update_flex() || update_teamfightTactics()) {
            // true = with error
            Toast.makeText(this, "Please review your data!", Toast.LENGTH_LONG).show();

            update_username();
            update_level();
            update_soloDuo();
            update_teamfightTactics();
        } else {
            // false = no error

            // TODO

            LeagueOfLegends.Account.Data_LeagueOfLegends lolData = new LeagueOfLegends.Account.Data_LeagueOfLegends(
                    new LeagueOfLegends.Account.Data_LeagueOfLegends.Data_(
                            new LeagueOfLegends.Update[] {
                                    new LeagueOfLegends.Update(
                                            LeagueOfLegends.CURRENT_SEASON,
                                            LeagueOfLegends.CURRENT_SPLIT,
                                            Util.Date.getCurrentDateObj(),
                                            new LeagueOfLegends.Rank(
                                                    (byte) SoloDuo_current_today_tier.getSelectedItemPosition(),
                                                    (byte) SoloDuo_current_today_division.getSelectedItemPosition(),
                                                    Short.parseShort(SoloDuo_current_today_lp.getText().toString()),
                                                    SoloDuo_current_today_series
                                            )
                                    )
                            }, new LeagueOfLegends.Rank[] {}
                    ), // solo duo
                    new LeagueOfLegends.Account.Data_LeagueOfLegends.Data_(
                            new LeagueOfLegends.Update[] {
                                    new LeagueOfLegends.Update(
                                            LeagueOfLegends.CURRENT_SEASON,
                                            LeagueOfLegends.CURRENT_SPLIT,
                                            Util.Date.getCurrentDateObj(),
                                            new LeagueOfLegends.Rank(
                                                    (byte) Flex_current_today_tier.getSelectedItemPosition(),
                                                    (byte) Flex_current_today_division.getSelectedItemPosition(),
                                                    Short.parseShort(Flex_current_today_lp.getText().toString()),
                                                    Flex_current_today_series
                                            )
                                    )
                            }, new LeagueOfLegends.Rank[] {}
                    ) // flex
            );
            LeagueOfLegends.Account.Data_TeamfightTactics tftData = new LeagueOfLegends.Account.Data_TeamfightTactics(
                    new LeagueOfLegends.Update[] {
                            new LeagueOfLegends.Update(
                                    TeamfightTactics.CURRENT_SET,
                                    TeamfightTactics.CURRENT_SPLIT,
                                    Util.Date.getCurrentDateObj(),
                                    new LeagueOfLegends.Rank(
                                            (byte) TeamfightTactics_current_today_tier.getSelectedItemPosition(),
                                            (byte) TeamfightTactics_current_today_division.getSelectedItemPosition(),
                                            Short.parseShort(TeamfightTactics_current_today_lp.getText().toString())
                                    )
                            )
                    },
                    new LeagueOfLegends.Rank[] {}
            );
            
            for(int i = 0; i < SoloDuo_old_data_container.getChildCount(); i++) {
                View cont = SoloDuo_old_data_container.getChildAt(i);
                
                short s = (short) ((Spinner) cont.findViewById(R.id.season_spinner)).getSelectedItemPosition();
                byte t = (byte) ((Spinner) cont.findViewById(R.id.tier_spinner)).getSelectedItemPosition();
                byte d = (byte) ((Spinner) cont.findViewById(R.id.division_spinner)).getSelectedItemPosition();
                byte eu = (byte) ((Spinner) cont.findViewById(R.id.emblemUpgrade_spinner)).getSelectedItemPosition();
                
                lolData.SoloDuo.addRank(s, new LeagueOfLegends.Rank(t, d, eu));
            }
            for(int i = 0; i < Flex_old_data_container.getChildCount(); i++) {
                View cont = Flex_old_data_container.getChildAt(i);

                short s = (short) ((Spinner) cont.findViewById(R.id.season_spinner)).getSelectedItemPosition();
                byte t = (byte) ((Spinner) cont.findViewById(R.id.tier_spinner)).getSelectedItemPosition();
                byte d = (byte) ((Spinner) cont.findViewById(R.id.division_spinner)).getSelectedItemPosition();
                byte eu = (byte) ((Spinner) cont.findViewById(R.id.emblemUpgrade_spinner)).getSelectedItemPosition();

                lolData.Flex.addRank(s, new LeagueOfLegends.Rank(t, d, eu));
            }
            for(int i = 0; i < TeamfightTactics_old_data_container.getChildCount(); i++) {
                View cont = TeamfightTactics_old_data_container.getChildAt(i);

                short s = (short) ((Spinner) cont.findViewById(R.id.season_spinner)).getSelectedItemPosition();
                byte t = (byte) ((Spinner) cont.findViewById(R.id.tier_spinner)).getSelectedItemPosition();
                byte d = (byte) ((Spinner) cont.findViewById(R.id.division_spinner)).getSelectedItemPosition();
                byte eu = (byte) ((Spinner) cont.findViewById(R.id.emblemUpgrade_spinner)).getSelectedItemPosition();

                tftData.addRank(s, new LeagueOfLegends.Rank(t, d, eu));
            }
            
            if(accountIndex == -1) { // if new account
                LeagueOfLegends.addNewAccount(new LeagueOfLegends.Account(
                        username_field.getText().toString(),
                        0,
                        Short.parseShort(level_field.getText().toString()),
                        club_field.getText().toString(),
                        (byte) server_field.getSelectedItemPosition(),
                        matchHistory_field.getText().toString(),
                        (byte) SoloDuo_current_emblemUpgrade.getSelectedItemPosition(),
                        lolData,
                        tftData
                ));
            }
            
            

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

            LeagueOfLegends.saveData(getApplicationContext());

            finish();
        }

    }

    protected boolean update_username() {
        if(username_field.getText().toString().length() == 0) {
            username_field.setBackground(getDrawable(R.drawable.add_account_field_error));
            return true;
        } else {
            username_field.setBackground(getDrawable(R.drawable.add_account_field_default));
        }

        return false;
    }
    protected boolean update_level() {
        if(level_field.getText().toString().length() == 0) {
            level_field.setBackground(getDrawable(R.drawable.add_account_field_error));
            return true;
        } else {
            level_field.setBackground(getDrawable(R.drawable.add_account_field_default));
        }

        return false;
    }
    protected boolean update_soloDuo() {
        return checkOldDataForErrors(SoloDuo_old_data_seasons.length, SoloDuo_old_data_container) ||
                updateLP(SOLO_DUO, (byte) SoloDuo_current_today_tier.getSelectedItemPosition(), (byte) SoloDuo_current_today_division.getSelectedItemPosition(), SoloDuo_current_today_lp, null, null);
    }
    protected boolean update_flex() {
        return checkOldDataForErrors(Flex_old_data_seasons.length, Flex_old_data_container) ||
                updateLP(FLEX, (byte) Flex_current_today_tier.getSelectedItemPosition(), (byte) Flex_current_today_division.getSelectedItemPosition(), Flex_current_today_lp, null, null);
    }
    protected boolean update_teamfightTactics() {
        return checkOldDataForErrors(TeamfightTactics_old_data_sets.length, TeamfightTactics_old_data_container) ||
                updateLP(TEAMFIGHT_TACTICS, (byte) TeamfightTactics_current_today_tier.getSelectedItemPosition(), (byte) TeamfightTactics_current_today_division.getSelectedItemPosition(), TeamfightTactics_current_today_lp, null, null);
    }
    protected boolean checkOldDataForErrors(int arrayLength, final LinearLayout oldDataContainer) {
        boolean hasError = false;

        if(oldDataContainer.getChildCount() == 0) return false;

        byte[] seasonsTick = new byte[arrayLength];
        Arrays.fill(seasonsTick, (byte) 0);

        int childrenCount = oldDataContainer.getChildCount();
        for(int i = 0; i < childrenCount; i++) {
            View cont = oldDataContainer.getChildAt(i);
            if(cont == null) continue;

            Spinner seasonSpinner = cont.findViewById(R.id.season_spinner);
            int selectedSeasonIndex = seasonSpinner.getSelectedItemPosition();

            seasonsTick[selectedSeasonIndex]++;

            seasonSpinner.setBackgroundResource(R.drawable.add_account_field_default);
        }

        for(int ii = 0; ii < arrayLength; ii++) {
            byte b = seasonsTick[ii];
            if(b <= 1) continue; // single field

            for(int i = 0; i < childrenCount; i++) {
                View cont = oldDataContainer.getChildAt(i);
                if(cont == null) continue;

                Spinner seasonSpinner = cont.findViewById(R.id.season_spinner);
                int selectedSeasonIndex = seasonSpinner.getSelectedItemPosition();

                if(selectedSeasonIndex == ii) {
                    seasonSpinner.setBackgroundResource(R.drawable.add_account_field_error);
                    hasError = true;
                }
            }
        }

        return hasError;
    }
    protected boolean updateLP(byte id, byte tier, byte division, EditText lp, LinearLayout seriesContainer, ImageView[] seriesButton) {
        if(lp.getText().toString().length() == 0) {
            lp.setBackgroundResource(R.drawable.add_account_field_error);
            return true;
        } else {
            if(tier != LeagueOfLegends.Rank.TIER_MASTER && tier != LeagueOfLegends.Rank.TIER_GRANDMASTER && tier != LeagueOfLegends.Rank.TIER_CHALLENGER) {
                if(Integer.parseInt(lp.getText().toString()) < -10 || Integer.parseInt(lp.getText().toString()) > 100) {
                    lp.setBackgroundResource(R.drawable.add_account_field_error);
                    return true;
                } else {
                    lp.setBackgroundResource(R.drawable.add_account_field_default);
                    if(id != TEAMFIGHT_TACTICS && Integer.parseInt(lp.getText().toString()) == 100) {
                        if(seriesContainer == null || seriesButton == null) return false;

                        seriesContainer.setVisibility(View.VISIBLE);
                        if (division == 0) {
                            seriesButton[3].setVisibility(View.VISIBLE);
                            seriesButton[4].setVisibility(View.VISIBLE);
                        } else {
                            seriesButton[3].setVisibility(View.GONE);
                            seriesButton[4].setVisibility(View.GONE);
                        }
                    } else if(id != TEAMFIGHT_TACTICS) {
                        if(seriesContainer == null || seriesButton == null) return false;

                        seriesContainer.setVisibility(View.GONE);
                        flushSeries(id, seriesButton);
                    }

                    return false;
                }
            } else {
                lp.setBackgroundResource(R.drawable.add_account_field_default);

                if(seriesContainer == null || seriesButton == null) return false;

                seriesContainer.setVisibility(View.GONE);
                flushSeries(id, seriesButton);

                return false;
            }
        }
    }


    protected void showIconSelectDialog() {
        try {
//            if(SelectIconBottomSheetDialog == null)
//                SelectIconBottomSheetDialog = new BottomSheetDialog(this);
//            else if(SelectIconBottomSheetDialog.isShowing())
//                return;
//
//            final View SelectIconBottomSheetView = LayoutInflater.from(this).inflate(R.layout.lol_select_icon, (ViewGroup) findViewById(R.id.parent));
//
//            SelectIconBottomSheetDialog.setCanceledOnTouchOutside(true);
//            SelectIconBottomSheetDialog.setContentView(SelectIconBottomSheetView);
//            SelectIconBottomSheetDialog.show();
//
//            View cont = SelectIconBottomSheetView.findViewById(R.id.icon_list_container);
//
//            Bitmap bmp = BitmapFactory.decodeFile(this.getExternalFilesDir(null).getPath() + "/assets/lol/profileicon0.png");
//            ImageView img = cont.findViewById(R.id.icons);//new ImageView(this);
//            img.setImageBitmap(bmp);
//            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
////            img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//            ((ViewGroup) cont).addView(img);
        } catch(Exception e) { Dev.log("LolAddAccount.showIconSelectDialog", e); }

//        Toast.makeText(this, "TODO - dialog", Toast.LENGTH_SHORT).show();
    }
}
