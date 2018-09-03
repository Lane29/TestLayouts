package com.packtpub.testlayouts;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int numOfMatchedCards = 0;

    private int numRows = 0;
    private int numCols = 0;

    private int lengthOfPack = 0;

    private int pack[];
    private int openedCards[];

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGame();

        shuffleCards();
    }





    private void initGame(){
        //TODO! Read from prefereces.     For level 1 of the game there two sets = two mathched cards
        numOfMatchedCards = 2;
        numRows = 3;
        numCols = 4;

        //For level #1 = 12 cards are laid
        lengthOfPack = numRows * numCols;

        //For level #1 = 6 cards in a set
        int numberOfCardsInSet = lengthOfPack / numOfMatchedCards;

        //Array for all cards
        pack = new int[lengthOfPack];

        //Array for opened cards
        openedCards = new int[numOfMatchedCards];

        //Initialising pack array. For level #1 = [1;2;3;4;5;6;1;2;3;4;5;6]
        for (int i = 0; i < lengthOfPack; i++) {
            pack[i] = (i + 1) % numberOfCardsInSet;
        }

        //Initialising array of opened cards.
        for (int i = 0; i < numOfMatchedCards; i++) {
            openedCards[i] = 0;
        }

        //Setting onClickListener on each imageView
        ImageView imageView;
        for (int i = 1; i <= numRows; i++)
            for (int j = 1; j <= numCols; j++) {
                int resID = getResources().getIdentifier("position" + i + j, "id", getPackageName());
                imageView = findViewById(resID);
                imageView.setOnClickListener(this);
            }
    }

    public void shuffleCards() {
        Random random = new Random();

        for (int i = 0; i < lengthOfPack; i++) {

            int cardPosition = random.nextInt(lengthOfPack - 1);

            int curCard = pack[i];
            pack[i] = pack[cardPosition];
            pack[cardPosition] = curCard;
        }
    }


    public void onClick(View view) {
        //ID of the clicked card (IDs look like "position<XY>" where X - number of the row, Y - number of the column)
        String cardPosition = getResources().getResourceEntryName(view.getId());//position32
        int length = 10;//cardPosition.length();
        Log.i("info", "cardPosition = " + cardPosition);

        int cardRow = Integer.parseInt(cardPosition.substring(length - 2, length-1));//3
        int cardCol = Integer.parseInt(cardPosition.substring(length-1));//2
        Log.i("info", cardRow + ":" + cardCol + " is clicked!!!");

        //Position of the clicked card in a whole pack
        int positionInPack = numCols * (cardRow - 1) + cardCol;//10
        Log.i("info", "Position in Pack = " + positionInPack);

        //Make sound
        mediaPlayer = (MediaPlayer) MediaPlayer.create(this, R.raw.memory_flip);
        mediaPlayer.start();

        //Turn the card face up (name of the target resource looks like "image<X>")
        int id = getResources().getIdentifier("image" + pack[positionInPack-1], "drawable", getPackageName());
        ((ImageView) view).setImageResource(id);

        /*//add open card to array openedCards
        addOpenedCard();

        if (areOpenedCardMatch()){
            //remove matched cards
        }
        else {
            //close opened cards; init openedCards array
        }*/
    }

   /* private boolean areOpenedCardMatch() {
        //
    }

    private void addOpenedCard() {
        //
    }
*/

    //New game
    public void onStartClick(View view) {


        shuffleCards();


        ImageView imageView;

        //Turn all cards face down
        for (int i = 1; i <= numRows; i++)
            for (int j =1; j <= numCols; j++) {
                int resID = getResources().getIdentifier("position" + i + j, "id", getPackageName());
                imageView = findViewById(resID);

                int id = getResources().getIdentifier("square", "drawable", getPackageName());
                imageView.setImageResource(id);
            }


    }
}
