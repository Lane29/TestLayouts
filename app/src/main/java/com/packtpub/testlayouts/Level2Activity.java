package com.packtpub.testlayouts;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Level2Activity extends AppCompatActivity implements View.OnClickListener {

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

    boolean isPlayStarted = false;
    boolean isOpenedCardsValuesArrayInitiated = true;

    TextView textTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2);

        numOfMatchedCards = 3;
        numRows = 3;
        numCols = 5;

        textTime = (TextView) findViewById(R.id.textTime);
            textTime.setText("Time: 00:00");

        initGame();
            cardTools.shuffleCards(pack);

        turnAllCardsFaceDown();
    }

    private void initGame(){
        // Log.i("info", "initGame...............");
        lengthOfPack = numRows * numCols;           //For level #2 = 15 cards are laid
        numberOfCardsInSet = lengthOfPack / numOfMatchedCards;    //For level #2 = 5 cards in a set

        //Array for all cards
        pack = cardTools.initPackArray(lengthOfPack, numberOfCardsInSet);

        //Array for flags for played cards
        playedCards = cardTools.initPlayedCardsArray(lengthOfPack);;

        //Array for opened cards
        openedCardsValues = cardTools.initOpenedCardsValuesArray(numOfMatchedCards);
        isOpenedCardsValuesArrayInitiated = true;

        //Array for positions of opened cards
        openedCardsPositions = cardTools.initOpenedCardsPositionsArray(numOfMatchedCards);

        initTextMoves();
        setOnClickListenerOnImageViews();
        isPlayStarted = false;
    }

    private void initTextMoves() {
        cntMoves = 0;
        TextView textMoves = findViewById(R.id.textMoves);
        String str = "Moves: 0";
        textMoves.setText(str);
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

    public void onClick(View view) {
        Log.i("info", "onClick...............");

        if (!isPlayStarted){
            setTimer();
            isPlayStarted = true;
        }
        ImageView clickedCard = (ImageView) view;

        //Position of the clicked card in a whole pack
        int positionInPack = Integer.parseInt(clickedCard.getTag().toString());
        Log.i("info", "Position in Pack = " + positionInPack);

        //Make sound
        flipMediaPlayer = MediaPlayer.create(this, R.raw.memory_flip);
        flipMediaPlayer.start();

        //Turn the card face up (name of the target resource looks like "image<X>")
        int id = getResources().getIdentifier("image" + pack[positionInPack - 1], "drawable", getPackageName());
        clickedCard.setImageResource(id);
        Log.i("info", "image" + pack[positionInPack - 1]);

        //add position of the opened card to the array openedCards
        cardTools.addOpenedCard(pack, positionInPack, openedCardsValues, openedCardsPositions, numOfMatchedCards);

        if (cardTools.neededNumberOfCardsIsOpened(openedCardsValues, numOfMatchedCards)) {
            if (cardTools.areOpenedCardMatch(openedCardsValues, numOfMatchedCards)) {
                //Log.i("info", "neededNumberOfCardsIsOpened...Cards are matched!!! ");
                removeMatchedCards();
                cardTools.addPlayedCards(openedCardsPositions, playedCards, numOfMatchedCards);

                openedCardsValues = cardTools.initOpenedCardsValuesArray(numOfMatchedCards);
                openedCardsPositions = cardTools.initOpenedCardsPositionsArray(numOfMatchedCards);

                MediaPlayer matchMediaPlayer = MediaPlayer.create(this, R.raw.memory_match);
                matchMediaPlayer.start();

                if (cardTools.areAllCardsPlayed(playedCards, lengthOfPack)) {
                    Log.i("info", "WIN-WIN-WIN !!!");
                    isPlayStarted = false;
                    removeOnClickListenerOnImageViews();
                    Button buttonStart = findViewById(R.id.button);
                    buttonStart.setText("Finish game");
                    buttonStart.setTag(1);
                    //TODO! Winning
                    Toast toast = Toast.makeText(getApplicationContext(), "CONGRATULATIONS!!!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        openedCardsValues = cardTools.initOpenedCardsValuesArray(numOfMatchedCards);
                        openedCardsPositions = cardTools.initOpenedCardsPositionsArray(numOfMatchedCards);
                        turnAllCardsFaceDown();
                    }
                }, 1500);
                flipMediaPlayer.start();
            }
        }

        cntMoves++;
        showMoves();
    }

    //Re-start the game
    public void onStartClick(View view) {
        Log.i("info", "onStartClick...............");
        if (view.getTag().toString().equals("0")) {
            cardTools.shuffleCards(pack);
            playedCards = cardTools.initPlayedCardsArray(lengthOfPack);
            moveBackAllCards();
            turnAllCardsFaceDown();
            openedCardsValues = cardTools.initOpenedCardsValuesArray(numOfMatchedCards);
            initTextMoves();
            setOnClickListenerOnImageViews();
            textTime.setText("Time: 00:00");
        }
    }

    private void removeMatchedCards() {
        ImageView imageView;
        for (int i = 0; i < numOfMatchedCards; i++) {
            Log.i("info", "........removeMatchedCards...openedCardsPositions["+i+"] = " + openedCardsPositions[i]);
            int resID = getResources().getIdentifier(cardTools.getCardIDByPosition(openedCardsPositions[i], numCols), "id", getPackageName());
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

    private void showMoves() {
        TextView textMoves = findViewById(R.id.textMoves);
        String strMoves = "Moves: " + cntMoves;
        textMoves.setText(strMoves);
    }

    //Turn all cards face down
    private void turnAllCardsFaceDown() {
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

    private void setTimer() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private long time = 0;
            @Override
            public void run()
            {
                time += 1000;
                if (isPlayStarted) {
                    textTime.setText("Time: " + cardTools.getTextTime(time));
                }
                h.postDelayed(this, 1000);
            }
        }, 1000); // 1 second
    }


}
