package com.devnamme.stattracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {
    BottomSheetDialog selectAccountBottomSheetDialog;
    private byte selected_ID = -1;

    private int REQUEST_CODE_ADD_ACCOUNT_LOL = 10;
    private int REQUEST_CODE_ADD_ACCOUNT_LOR = 11;
    private int REQUEST_CODE_ADD_ACCOUNT_VAL = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create log file
        Dev.createLogFile(MainActivity.this);

        // download file
        Dev.checkDataFile(MainActivity.this);

        // load data
        LeagueOfLegends.loadData(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_ACCOUNT_LOL || requestCode == REQUEST_CODE_ADD_ACCOUNT_LOR || requestCode == REQUEST_CODE_ADD_ACCOUNT_VAL) {
            showSelectAccount(selected_ID);
            
            Dev.log("MainActivity.onActivityResult " + resultCode);
        }
    }

    private void showSelectAccount(byte mode) {
        try {
            selected_ID = mode;

            Dev.log("MainActivity.showSelectAccount:" + Dev.IDToString(mode));

            if(selectAccountBottomSheetDialog == null)
                selectAccountBottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            else if(selectAccountBottomSheetDialog.isShowing())
                return;

            final View selectAccountBottomSheetView = LayoutInflater.from(MainActivity.this).inflate(R.layout.select_account, null);

            selectAccountBottomSheetDialog.setCanceledOnTouchOutside(true);
            selectAccountBottomSheetDialog.setContentView(selectAccountBottomSheetView);
            selectAccountBottomSheetDialog.show();

            LinearLayout accounts_container = selectAccountBottomSheetDialog.findViewById(R.id.select_account_container);
            if (mode == LeagueOfLegends.ID || mode == TeamfightTactics.ID) {
                int index = -1;
                for (final LeagueOfLegends.Account account : LeagueOfLegends.accounts) {
                    index++;


                    View container = LayoutInflater.from(this).inflate(R.layout.select_account_lol, accounts_container, false);
                    ImageView rankedEmblem = container.findViewById(R.id.rankedEmblem);
                    TextView username = container.findViewById(R.id.username);
                    TextView level = container.findViewById(R.id.level);
                    ImageView serverFlag = container.findViewById(R.id.serverFlag);
                    TextView serverText = container.findViewById(R.id.serverText);

                    /*
                    * CONTAINER
                     */
                    container.setClickable(true);
                    final int finalIndex = index;
                    container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dev.log("account " + account.username + " OnClick");
                            // TODO - select account, move activity

                            // DEV
                            Intent intent = new Intent(getApplicationContext(), LolAddAccount.class);
                            intent.putExtra("accountIndex", finalIndex);

                            startActivityForResult(intent, REQUEST_CODE_ADD_ACCOUNT_LOL);

                            selectAccountBottomSheetDialog.dismiss();
                        }
                    });

                    /*
                    * RANKED EMBLEM
                     */
                    BitmapDrawable rankedEmblemUpgrade_BitmapDrawable;
                    if(mode == LeagueOfLegends.ID) rankedEmblemUpgrade_BitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(account.LeagueOfLegends.getCurrentRank().getEmblemUpgrade(account.emblemUpgrades), null);
                    else rankedEmblemUpgrade_BitmapDrawable = (BitmapDrawable) this.getResources().getDrawable(account.TeamfightTactics.getCurrentRank().getEmblemUpgrade(account.emblemUpgrades), null);

                    int rankedEmblemUpgrade_scaledWidth = new Util.ScaledDimensions(rankedEmblemUpgrade_BitmapDrawable.getBitmap().getWidth(), rankedEmblemUpgrade_BitmapDrawable.getBitmap().getHeight()).getScaledDimension(Util.ScaledDimensions.GIVEN_HEIGHT, 208);

                    RelativeLayout.LayoutParams rankedEmblem_LayoutParams = new RelativeLayout.LayoutParams(
                            rankedEmblemUpgrade_scaledWidth,
                            208
                    );
                    rankedEmblem_LayoutParams.setMargins(
                            Util.convertDpToPx(MainActivity.this, 12.0f) + ((208 - rankedEmblemUpgrade_scaledWidth) / 2),
                            0,
                            Util.convertDpToPx(MainActivity.this, 12.0f) + ((208 - rankedEmblemUpgrade_scaledWidth) / 2),
                            0
                    );

                    rankedEmblem.setLayoutParams(rankedEmblem_LayoutParams);
                    if(mode == LeagueOfLegends.ID) {
                        if(account.LeagueOfLegends.getCurrentRank().backgroundUpgradeNeeded(account.emblemUpgrades)) {
                            rankedEmblem.setImageResource(account.LeagueOfLegends.getCurrentRank().getEmblem());
                            rankedEmblem.setBackgroundResource(account.LeagueOfLegends.getCurrentRank().getEmblemUpgrade(account.emblemUpgrades));
                        } else
                            rankedEmblem.setImageResource(account.LeagueOfLegends.getCurrentRank().getEmblemUpgrade(account.emblemUpgrades, true));
                    } else {
                        if(account.TeamfightTactics.getCurrentRank().backgroundUpgradeNeeded(account.emblemUpgrades)) {
                            rankedEmblem.setImageResource(account.TeamfightTactics.getCurrentRank().getEmblem());
                            rankedEmblem.setBackgroundResource(account.TeamfightTactics.getCurrentRank().getEmblemUpgrade(account.emblemUpgrades));
                        } else
                            rankedEmblem.setImageResource(account.TeamfightTactics.getCurrentRank().getEmblemUpgrade(account.emblemUpgrades, true));
                    }

                    /*
                    * USERNAME
                     */
                    username.setText(account.username);

                    /*
                    * LEVEL
                     */
                    level.setText("Level " + account.level);

                    /*
                    * SERVER FLAG
                     */
                    serverFlag.setImageResource(Server.getServerFlag(account.server));

                    /*
                    * SERVER TEXT
                     */
                    serverText.setText(Server.toString(account.server));


                    if(accounts_container != null)
                        accounts_container.addView(container, 0);
                }
            } else if (mode == LegendsOfRuneterra.ID) {
                Toast.makeText(MainActivity.this, "TODO", Toast.LENGTH_SHORT).show();
                // TODO
            } else if(mode == Valorant.ID) {
                Toast.makeText(MainActivity.this, "TODO", Toast.LENGTH_SHORT).show();
                // TODO
            }
        } catch(Exception e) { Dev.log("MainActivity.showSelectAccount", e); }
    }

    public void selectLol(View view) {
        showSelectAccount(LeagueOfLegends.ID);
    }
    public void selectTft(View view) {
        showSelectAccount(TeamfightTactics.ID);
    }
    public void selectLor(View view) {
        showSelectAccount(LegendsOfRuneterra.ID);
    }
    public void selectValorant(View view) {
        showSelectAccount(Valorant.ID);
    }

    public void addAccount(View view) {
        try {
            Intent intent = null;

            if(selected_ID == LeagueOfLegends.ID || selected_ID == TeamfightTactics.ID)
                intent = new Intent(MainActivity.this, LolAddAccount.class);
            else if(selected_ID == LegendsOfRuneterra.ID) {}
            // TODO - legends of runeterra add account
            else if(selected_ID == Valorant.ID) {}
            // TODO - valorant add account

            if(intent != null) {
                Dev.log("startActivityForResult:" + Dev.IDToString(selected_ID));

                intent.putExtra("accountIndex", -1);

                if(selected_ID == LeagueOfLegends.ID || selected_ID == TeamfightTactics.ID)
                    startActivityForResult(intent, REQUEST_CODE_ADD_ACCOUNT_LOL);
                else if(selected_ID == LegendsOfRuneterra.ID)
                    startActivityForResult(intent, REQUEST_CODE_ADD_ACCOUNT_LOR);
                else if(selected_ID == Valorant.ID)
                    startActivityForResult(intent, REQUEST_CODE_ADD_ACCOUNT_VAL);

                selectAccountBottomSheetDialog.dismiss();
            }
        } catch(Exception e) { Dev.log("MainActivity.addAccount", e); }
    }
}
