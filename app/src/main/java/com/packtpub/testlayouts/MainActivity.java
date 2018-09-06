package com.packtpub.testlayouts;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean playedCards[];

    private int openedCardsValues[];
    private int openedCardsPositions[];

    private int cntMoves = 0;
    private int timeInMilliseconds = 0;

    private MediaPlayer flipMediaPlayer;

    boolean isAnimationInProgress = false;

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
        numOfMatchedCards = 2;
        numRows = 3;
        numCols = 4;

        lengthOfPack = numRows * numCols;           //For level #1 = 12 cards are laid

        numberOfCardsInSet = lengthOfPack / numOfMatchedCards;    //For level #1 = 6 cards in a set

        pack = new int[lengthOfPack];   //Array for all cards
        initPackArray();

        playedCards = new boolean[lengthOfPack];   //Array for flags for played cards
        initPlayedCardsArray();

        openedCardsValues = new int[numOfMatchedCards];   //Array for opened cards
        initOpenedCardsValuesArray();

        openedCardsPositions = new int[numOfMatchedCards];   //Array for positions of opened cards
        initOpenedCardsPositionsArray();

        setOnClickListenerOnImageViews();

        initGameTime();

        initMoves();
    }

    private void initPlayedCardsArray() {
        // Log.i("info", "initPlayedCardsArray...............");
        for (int i = 0; i < lengthOfPack; i++) {
            playedCards[i] = false;
        }
    }

    public void initPackArray(){
       // Log.i("info", "initPackArray...............");
        for (int i = 0; i < lengthOfPack; i++) {    //Initialising pack array. For level #1 = [1;2;3;4;5;6;1;2;3;4;5;6]
            pack[i] = (i + 1) % numberOfCardsInSet + 1;
        }
    }


    public void initOpenedCardsValuesArray(){
       /// Log.i("info", "initOpenedCardsValuesArray...............");
        for (int i = 0; i < numOfMatchedCards; i++) {
            openedCardsValues[i] = 0;
        }
    }

    private void initOpenedCardsPositionsArray() {
        /// Log.i("info", "initOpenedCardsPositionsArray...............");
        for (int i = 0; i < numOfMatchedCards; i++) {
            openedCardsPositions[i] = 0;
        }
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

        if (!isAnimationInProgress) {

            //Make sound
            flipMediaPlayer = MediaPlayer.create(this, R.raw.memory_flip);
            flipMediaPlayer.start();

            Log.i("info", "image" + pack[positionInPack - 1]);
            //Turn the card face up (name of the target resource looks like "image<X>")
            int id = getResources().getIdentifier("image" + pack[positionInPack - 1], "drawable", getPackageName());
            clickedCard.setImageResource(id);

            //add open card to array openedCards
            addOpenedCard(positionInPack);
            for (int i = 0; i < numOfMatchedCards; i++) {
                Log.i("info", "openedCardsValues[" + i + "] = " + openedCardsValues[i]);
            }


            if (neededNumberOfCardsIsOpened()) {
                if (areOpenedCardMatch()) {
                    Log.i("info", "Cards are matched!!! ");

                    removeMatchedCards();
                    addPlayedCards();

                    initOpenedCardsValuesArray();
                    initOpenedCardsPositionsArray();

                    MediaPlayer matchMediaPlayer = MediaPlayer.create(this, R.raw.memory_match);
                    matchMediaPlayer.start();

                    if (areAllCardsPlayed()) {
                        Log.i("info", "WIN-WIN-WIN !!!");
                        removeOnClickListenerOnImageViews();
                        Toast toast = Toast.makeText(getApplicationContext(), "CONGRATULATIONS!!!", Toast.LENGTH_LONG);
                        toast.show();
                    }

                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            initOpenedCardsValuesArray();
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
        }
        isAnimationInProgress = false;

        Log.i("info", "...");
    }

    private void removeOnClickListenerOnImageViews() {
        //Log.i("info", "setOnClickListenerOnImageViews...............");
        //remove onClickListener from each imageView
        ImageView imageView;
        for (int i = 1; i <= numRows; i++)
            for (int j = 1; j <= numCols; j++) {
                int resID = getResources().getIdentifier("card" + i + j, "id", getPackageName());
                imageView = findViewById(resID);
                imageView.setOnClickListener(null);
            }
    }

    private boolean areAllCardsPlayed() {
        boolean res = true;
        int i=0;
        while (i < lengthOfPack && res) {
            if (!playedCards[i]) res = false;
            i++;
        }
        return res;
    }

    private void addPlayedCards() {
        for (int i = 0; i < numOfMatchedCards; i++) {
            int position = openedCardsPositions[i];
            playedCards[position-1] = true;
        }
    }



    private void removeMatchedCards() {
        ImageView imageView;
        for (int i = 0; i < numOfMatchedCards; i++) {
            Log.i("info", "........removeMatchedCards...openedCardsPositions["+i+"] = " + openedCardsPositions[i]);
            int resID = getResources().getIdentifier(getCardIDByPosition(openedCardsPositions[i]), "id", getPackageName());
            imageView = findViewById(resID);
            imageView.animate().translationXBy(-1000f).setDuration(1000);
        }
    }

    private void moveBackAllCards() {
        Log.i("info", "........moveBackAllCards");
        ImageView imageView;
        for (int i = 1; i <= numRows; i++)
            for (int j =1; j <= numCols; j++) {
                int resID = getResources().getIdentifier("card" + i + j, "id", getPackageName());
                imageView = findViewById(resID);
                imageView.animate().translationXBy(1000f).setDuration(0);
            }
    }

    private String getCardIDByPosition(int openedCardsPosition) {
        Log.i("info", "........getCardIDByPosition...openedCardsPosition = " + openedCardsPosition);
        int cardCol = openedCardsPosition % numCols;
        int cardRow = openedCardsPosition / numCols;
        if (cardCol == 0) {
            cardCol = numCols;
        }
        else {
            cardRow++;
        }
        String cardId = "card" + cardRow + cardCol;
        Log.i("info", "........getCardIDByPosition...cardId = " + cardId);
        return cardId;
    }



    /*private void showGameTime() {
        TextView textTime = (TextView) findViewById(R.id.textTime);
        textTime.setText("Time: " + timeInMilliseconds/1000 + " seconds");
    }*/

    private void showMoves() {
        TextView textMoves = findViewById(R.id.textMoves);
        String strMoves = "Moves: " + cntMoves;
        textMoves.setText(strMoves);
    }

    private boolean neededNumberOfCardsIsOpened() {   //3 cards <> 0
        //Log.i("info", "neededNumberOfCardsIsOpened...............");
        boolean res = true;
        int i=0;
        while (i<numOfMatchedCards && res) {
            if (openedCardsValues[i] == 0) res = false;
            i++;
        }
        return res;
    }


    private boolean areOpenedCardMatch() {
        //Log.i("info", "areOpenedCardMatch...............");
        boolean res = true;

        int i = 1;
        while (i < numOfMatchedCards && res) {
            if (openedCardsValues[i] != openedCardsValues[i-1]) res = false;
            Log.i("info", "openedCardsValues[" + i + "] = "+ openedCardsValues[i] + "    i = " + i + "   res = " + res);
            i++;
        }
        return res;
    }


    private void addOpenedCard(int positionInPack) {
        Log.i("info", "addOpenedCard...............pospositionInPackInPack = " + positionInPack);
        int i = 0;
        while (i<numOfMatchedCards && openedCardsValues[i] != 0) i++;
        openedCardsValues[i] = pack[positionInPack-1];
        openedCardsPositions[i] = positionInPack;
    }


    //Re-start the game
    public void onStartClick(View view) {
        //Log.i("info", "onStartClick...............");
        shuffleCards();
        initPlayedCardsArray();
        moveBackAllCards();
        turnAllCardsFaceDown();
        initOpenedCardsValuesArray();
        initGameTime();
        initMoves();
        setOnClickListenerOnImageViews();

    }

    private void initGameTime() {
        timeInMilliseconds = 0;
        TextView textTime = findViewById(R.id.textTime);
        String str = "Time: 0 seconds";
        textTime.setText(str);
    }

    private void initMoves() {
        cntMoves = 0;
        TextView textMoves = findViewById(R.id.textMoves);
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
