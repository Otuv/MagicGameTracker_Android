package se.devotu.magicgametracker.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.devotu.magicgametracker.R;
import se.devotu.magicgametracker.bl.GameManager;
import se.devotu.magicgametracker.bl.OpponentManager;
import se.devotu.magicgametracker.enums.ManaColor;
import se.devotu.magicgametracker.info.Colorset;
import se.devotu.magicgametracker.info.Opponent;
import se.devotu.magicgametracker.info.PerformanceRating;

/**
 * Created by Devotu on 2016-01-04.
 */
public class AddGameFragment extends Fragment {

    //private Button bAddWin, bAddLose;
    //Version 4.0 private Button bAddGame; //Version 2.1
    private RadioButton cbWin, cbLoss; //Version 2.1
    private int deckID;
    private CheckBox cbBlack, cbWhite, cbRed, cbBlue, cbGreen, cbDevoid;
    private EditText etComment;
    //Version 4.0 private TextView tvTitle;
    private Spinner spOpponent;
    private ArrayList<Opponent> opponents; //Version 2.0
    private RatingBar rbarPerformanceRating; //Version 3.1

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_addgame, container, false);

        Intent passedIntent = getActivity().getIntent();
        deckID = passedIntent.getIntExtra("Deck_ID", -1);

        //Version 2.0
        spOpponent = (Spinner)v.findViewById(R.id.ddOpponent);
        addItemsToOpponentSpinner(spOpponent);

        //Version 3.1
        rbarPerformanceRating = (RatingBar)v.findViewById(R.id.rbarGamePerformanceRating);

        cbWin = (RadioButton)v.findViewById(R.id.rbWin);
        cbLoss = (RadioButton)v.findViewById(R.id.rbLoss);

        //Version 4.0 Moved from addGame to remain available
        cbBlack = (CheckBox)v.findViewById(R.id.cbBlack);
        cbWhite = (CheckBox)v.findViewById(R.id.cbWhite);
        cbRed = (CheckBox)v.findViewById(R.id.cbRed);
        cbBlue = (CheckBox)v.findViewById(R.id.cbBlue);
        cbGreen = (CheckBox)v.findViewById(R.id.cbGreen);
        cbDevoid = (CheckBox)v.findViewById(R.id.cbDevoid);//Version 4.2

        etComment = (EditText)v.findViewById(R.id.etComment);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (cbWin.isChecked()) {
                    addGame(true);
                    String msg = getString(R.string.win) + " " + getString(R.string.registerd).toLowerCase();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else if (cbLoss.isChecked()) {
                    addGame(false);
                    String msg = getString(R.string.loss) + " " + getString(R.string.registerd).toLowerCase();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    String msg = getString(R.string.parametersMissing) + " " + getString(R.string.no) + " " + getString(R.string.game) + " " + getString(R.string.registerd);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateBack() {
        Intent intent = new Intent(this.getActivity(), DeckSwipePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.getActivity().finish();
    }

    private void addItemsToOpponentSpinner(Spinner spinner) {

        OpponentManager oppManager = new OpponentManager(this.getActivity());
        opponents= oppManager.getAllOpponents();

        //TODO remove and replace with custom adapter
        List<String> opponentNames = new ArrayList<String>();
        for (Opponent opponent : opponents) {
            opponentNames.add(opponent.getOpponentName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, opponentNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void addGame(Boolean win){

        Colorset opposingColorset = new Colorset();
        if (cbBlack.isChecked()) { opposingColorset.addColor(ManaColor.BLACK);}
        if (cbWhite.isChecked()) { opposingColorset.addColor(ManaColor.WHITE);}
        if (cbRed.isChecked()) { opposingColorset.addColor(ManaColor.RED);}
        if (cbBlue.isChecked()) { opposingColorset.addColor(ManaColor.BLUE);}
        if (cbGreen.isChecked()) { opposingColorset.addColor(ManaColor.GREEN);}
        if (cbDevoid.isChecked()) { opposingColorset.addColor(ManaColor.DEVOID);}

        String comment = etComment.getText().toString();

        // Version 2.0
        //TODO Måste finnas ett bättre sätt!
        int opponentIdPos = spOpponent.getSelectedItemPosition();
        int opponentId = opponents.get(opponentIdPos).getOpponent_ID();

        // Version 2.1
        //int performanceRating = slPerformanceRating.getProgress();

        //Version 3.1
        PerformanceRating performanceRating = new PerformanceRating();
        performanceRating.setPerformanceRatingFromStars(rbarPerformanceRating.getRating());

        GameManager gameManager = new GameManager(this.getActivity());
        gameManager.addNewGame(deckID, win, opposingColorset, comment, opponentId, (int)performanceRating.getPerformanceRatingAsRaw());
    }
}
