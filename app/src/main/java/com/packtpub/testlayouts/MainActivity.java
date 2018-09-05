package com.packtpub.testlayouts;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int numOfMatchedCards = 0;

    private int numRows = 0;
    private int numCols = 0;

    private int lengthOfPack = 0;
    private int numberOfCardsInSet = 0;

    private int pack[];
    private int openedCards[];
    private int openedCardsPositions[];

    private int cntMoves = 0;
    private int timeInMilliseconds = 0;

    private MediaPlayer flipMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGame();

        shuffleCards();
        turnAllCardsFaceDown();
    }



    private void initGame(){
       // Log.i("info", "initGame...............");
        //TODO! Read from prefereces. For level 1 of the game there two sets = two mathched cards
        numOfMatchedCards = 2;
        numRows = 3;
        numCols = 4;

        lengthOfPack = numRows * numCols;           //For level #1 = 12 cards are laid

        numberOfCardsInSet = lengthOfPack / numOfMatchedCards;    //For level #1 = 6 cards in a set

        pack = new int[lengthOfPack];   //Array for all cards
        initPackArray();

        openedCards = new int[numOfMatchedCards];   //Array for opened cards
        initOpenedCardsArray();

        openedCardsPositions = new int[numOfMatchedCards];   //Array for opened cards
        initOpenedCardsPositionsArray();

        setOnClickListenerOnImageViews();

        initGameTime();

        initMoves();
    }


    private void setOnClickListenerOnImageViews() {
        //Log.i("info", "setOnClickListenerOnImageViews...............");
        //Setting onClickListener on each imageView
        ImageView imageView;
        for (int i = 1; i <= numRows; i++)
            for (int j = 1; j <= numCols; j++) {
                int resID = getResources().getIdentifier("card" + i + j, "id", getPackageName());
                imageView = findViewById(resID);
                imageView.setOnClickListener(this);
            }
    }

    public void initPackArray(){
       // Log.i("info", "initPackArray...............");
        for (int i = 0; i < lengthOfPack; i++) {    //Initialising pack array. For level #1 = [1;2;3;4;5;6;1;2;3;4;5;6]
            pack[i] = (i + 1) % numberOfCardsInSet + 1;
        }
    }


    public void initOpenedCardsArray(){
       /// Log.i("info", "initOpenedCardsArray...............");
        for (int i = 0; i < numOfMatchedCards; i++) {
            openedCards[i] = 0;
        }
    }

    private void initOpenedCardsPositionsArray() {
        /// Log.i("info", "initOpenedCardsPositionsArray...............");
        for (int i = 0; i < numOfMatchedCards; i++) {
            openedCardsPositions[i] = 0;
        }
    }

    public void shuffleCards() {
       // Log.i("info", "shuffleCards...............");
        Random random = new Random();

        for (int i = 0; i < lengthOfPack; i++) {
            int cardPosition = random.nextInt(lengthOfPack - 1);
            int curCard = pack[i];
            pack[i] = pack[cardPosition];
            pack[cardPosition] = curCard;
        }
    }


    public void onClick(View view) {
        Log.i("info", "onClick...............");

        //TODO! Timing
        ///if (timeInMilliseconds == 0) {
            //start timer
        //}

        ImageView clickedCard = (ImageView) view;

        //Position of the clicked card in a whole pack
        int positionInPack = Integer.parseInt(clickedCard.getTag().toString());//10

        Log.i("info", "Position in Pack = " + positionInPack);

        //Make sound
        flipMediaPlayer = (MediaPlayer) MediaPlayer.create(this, R.raw.memory_flip);
        flipMediaPlayer.start();

        Log.i("info", "image" + pack[positionInPack-1]);
        //Turn the card face up (name of the target resource looks like "image<X>")
        int id = getResources().getIdentifier("image" + pack[positionInPack-1], "drawable", getPackageName());
        clickedCard.setImageResource(id);

        //add open card to array openedCards
        addOpenedCard(positionInPack);
        for (int i = 0; i < numOfMatchedCards; i++) {
            Log.i("info", "openedCards[" + i + "] = "+ openedCards[i]);
        }


        if (neededNumberOfCardsIsOpened()) {
            if (areOpenedCardMatch()) {
                Log.i("info", "Cards are matched!!! ");

                initOpenedCardsArray();
                initOpenedCardsPositionsArray();

                MediaPlayer matchMediaPlayer = (MediaPlayer) MediaPlayer.create(this, R.raw.memory_match);
                matchMediaPlayer.start();

                //clickedCard.animate().translationYBy(-1000f).setDuration(2000);

                //TODO! Animate opened cards
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        initOpenedCardsArray();
                        initOpenedCardsPositionsArray();
                        turnAllCardsFaceDown();
                    }
                }, 2000);

                flipMediaPlayer.start();
            }
        }

        cntMoves++;
        //showGameTime();
        showMoves();
        Log.i("info", "...");
    }

    /*private void showGameTime() {
        TextView textTime = (TextView) findViewById(R.id.textTime);
        textTime.setText("Time: " + timeInMilliseconds/1000 + " seconds");
    }*/

    private void showMoves() {
        TextView textMoves = (TextView) findViewById(R.id.textMoves);
        String strMoves = "Moves: " + cntMoves;
        textMoves.setText(strMoves);
    }

    private boolean neededNumberOfCardsIsOpened() {   //3 cards <> 0
        //Log.i("info", "neededNumberOfCardsIsOpened...............");
        boolean res = true;
        int i=0;
        while (i<numOfMatchedCards && res) {
            if (openedCards[i] == 0) res = false;
            i++;
        }
        return res;
    }


    private boolean areOpenedCardMatch() {
        //Log.i("info", "areOpenedCardMatch...............");
        boolean res = true;

        int i = 1;
        while (i < numOfMatchedCards && res) {
            if (openedCards[i] != openedCards[i-1]) res = false;
            Log.i("info", "openedCards[" + i + "] = "+ openedCards[i] + "    i = " + i + "   res = " + res);
            i++;
        }
        return res;
    }


    private void addOpenedCard(int positionInPack) {
        Log.i("info", "addOpenedCard...............pospositionInPackInPack = " + positionInPack);
        int i = 0;
        while (i<numOfMatchedCards && openedCards[i] != 0) i++;
        openedCards[i] = pack[positionInPack-1];
        openedCardsPositions[i] = positionInPack;
    }


    //Re-start the game
    public void onStartClick(View view) {
        //Log.i("info", "onStartClick...............");
        shuffleCards();

        turnAllCardsFaceDown();

        initOpenedCardsArray();

        initGameTime();

        initMoves();
    }

    private void initGameTime() {
        timeInMilliseconds = 0;
        TextView textTime = (TextView) findViewById(R.id.textTime);
        String str = "Time: 0 seconds";
        textTime.setText(str);
    }

    private void initMoves() {
        cntMoves = 0;
        TextView textMoves = (TextView) findViewById(R.id.textMoves);
        String str = "Moves: 0";
        textMoves.setText(str);
    }

    //Turn all cards face down
    public void turnAllCardsFaceDown() {
        ///Log.i("info", "turnAllCardsFaceDown...............");
        for (int i = 1; i <= numRows; i++)
            for (int j =1; j <= numCols; j++)  turnCardFaceDown(i, j);
    }

    private void turnCardFaceDown(int i, int j) {

        ///Log.i("info", "turnCardFaceDown...............");
        int resID = getResources().getIdentifier("card" + i + j, "id", getPackageName());
        ImageView imageView = findViewById(resID);

        int id = getResources().getIdentifier("square", "drawable", getPackageName());
        imageView.setImageResource(id);
    }
}
