package com.mad4124.mad4124project;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameLogic extends AppCompatActivity implements View.OnClickListener, ShakeDetector.Listener{

    ArrayList<String> arrayStrings = new ArrayList<String>();
    String docNumber = "";
    final static String TAG = "gameLog";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Game game;
    String player1Number="";
    String player2Number="";
    String player1Option="";
    String player2Option="";

    TextView txtTitle, txtPlayer1, txtPlayer2, txtNotification;
    ImageView imgPlayer1_image, imgPlayer2_image;
    Button btnNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayStrings.add("Rock");
        arrayStrings.add("Paper");
        arrayStrings.add("Scissors");
        arrayStrings.add("Lizard");
        arrayStrings.add("Spock");
        context = this;
        setContentView(R.layout.activity_game_logic);
        Bundle b = getIntent().getExtras();
        docNumber = b.getString("docNumber");
        Log.d(TAG, "DocumentSnapshot ID: " + docNumber);

        txtTitle = findViewById(R.id.txtTitle);
        txtPlayer1 = findViewById(R.id.txtPlayer1);
        txtPlayer2 = findViewById(R.id.txtPlayer2);
        txtNotification = findViewById(R.id.txtNotification);
        imgPlayer1_image = findViewById(R.id.imgPlayer1_image);
        imgPlayer2_image = findViewById(R.id.imgPlayer2_image);

        db.collection("games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Map<String, Object> data = document.getData();
                                if (document.getId().equals(docNumber)){
                                    if (document.getData().get("player1Number").equals(user.getPhoneNumber())){
                                        //user is player 1
                                        //game = new Game(user.getPhoneNumber(), data.get("player2Number").toString(),data.get("player1Option").toString(),data.get("player2Option").toString(),data.get("winner").toString());
                                        player1Number= user.getPhoneNumber();
                                        player2Number= data.get("player2Number").toString();
                                        player1Option= data.get("player1Option").toString();
                                        player2Option= data.get("player2Option").toString();
                                    }else {
                                        //user is player 2
                                        //game = new Game(data.get("player1Number").toString(), user.getPhoneNumber(),data.get("player1Option").toString(),data.get("player2Option").toString(),data.get("winner").toString());
                                        player1Number= data.get("player1Number").toString();
                                        player2Number= user.getPhoneNumber();
                                        player1Option= data.get("player1Option").toString();
                                        player2Option= data.get("player2Option").toString();
                                    }

                                    txtPlayer1.setText(player1Number);
                                    txtPlayer2.setText(player2Number);
                                    if (!player1Option.equals("")){
                                        imgPlayer1_image.setImageURI(Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/img" + player1Option.toLowerCase()));
                                    }
                                    if (!player2Option.equals("")){
                                        imgPlayer2_image.setImageURI(Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/img" + player2Option.toLowerCase()));
                                    }
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        btnNewGame = findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(this);

        txtPlayer1.setText(player1Number);
        txtPlayer2.setText(player2Number);
        if (!player1Option.equals("")){
            imgPlayer1_image.setImageURI(Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/img" + player1Option.toLowerCase()));
        }
        if (!player2Option.equals("")){
            imgPlayer2_image.setImageURI(Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/img" + player2Option.toLowerCase()));
        }

        final DocumentReference docRef = db.collection("games").document(docNumber);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    player1Option = data.get("player1Option").toString();
                    player2Option = data.get("player2Option").toString();
                    imgPlayer1_image.setImageURI(Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/img" + player1Option.toLowerCase()));
                    imgPlayer2_image.setImageURI(Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/img" + player2Option.toLowerCase()));
                    gameLogic();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector detector = new ShakeDetector(this);
        detector.start(manager);
        gameLogic();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnNewGame.getId()){
           hearShake();
            Toast.makeText(getApplicationContext(), "shaking on click", Toast.LENGTH_SHORT).show();
        }
    }

    public void gameLogic(){
        if(player1Option.equals("Paper") && player2Option.equals("Paper"))

        {
            txtNotification.setText("It's a Tie");
        }
        else if(player1Option.equals("Rock") && player2Option.equals("Rock"))
        {
            txtNotification.setText("It's a Tie");
        }
        else if(player1Option.equals("Scissors") && player2Option.equals("Scissors"))

        {
                txtNotification.setText("It's a Tie");
        }
        else if(player1Option.equals("Lizard") && player2Option.equals("Lizard"))

        {
            txtNotification.setText("It's a Tie");
        }
        else if(player1Option.equals("Spock") && player2Option.equals("Spock"))

        {
            txtNotification.setText("It's a Tie");
        }
// Rock
        else if(player1Option.equals("Rock") && player2Option.equals("Paper"))

        {
            txtNotification.setText("Player 2 Wins");
        }
        else if(player1Option.equals("Rock") && player2Option.equals("Lizard"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Rock") && player2Option.equals("Spock"))

        {
            txtNotification.setText("Player 2 Wins");
        }
        else if(player1Option.equals("Rock") && player2Option.equals("Scissors"))

        {
            txtNotification.setText("Player 1 Wins");
        }
// Paper

        else if(player1Option.equals("Paper") && player2Option.equals("Rock"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Paper") && player2Option.equals("Lizard"))

        {
            txtNotification.setText("Player 2 Wins");
        }
        else if(player1Option.equals("Paper") && player2Option.equals("Spock"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Paper") && player2Option.equals("Scissors"))

        {
            txtNotification.setText("Player 2 Wins");
        }
// Scissors

        else if(player1Option.equals("Scissors") && player2Option.equals("Paper"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Scissors") && player2Option.equals("Rock"))

        {
            txtNotification.setText("Player 2 Wins");
        }
        else if(player1Option.equals("Scissors") && player2Option.equals("Lizard"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Scissors") && player2Option.equals("Spock"))

        {
            txtNotification.setText("Player 2 Wins");
        }
// Spock

        else if(player1Option.equals("Spock") && player2Option.equals("Scissors"))

        {
            System.out.println("Player 1 Wins");
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Spock") && player2Option.equals("Paper"))
        {
            txtNotification.setText("Player 2 Wins");
        }
        else if(player1Option.equals("Spock") && player2Option.equals("Rock"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Spock") && player2Option.equals("Lizard"))

        {
            System.out.println("Player 2 Wins");
            txtNotification.setText("Player 2 Wins");
        }
// Lizard

        else if(player1Option.equals("Lizard") && player2Option.equals("Spock"))

        {
            txtNotification.setText("Player 2 Wins");
        }
        else if(player1Option.equals("Lizard") && player2Option.equals("Scissors"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Lizard") && player2Option.equals("Paper"))

        {
            txtNotification.setText("Player 1 Wins");
        }
        else if(player1Option.equals("Lizard") && player2Option.equals("Rock"))

        {
            txtNotification.setText("Player 2 Wins");
        }
        else {
            txtNotification.setText("Waiting for a player to shake");
        }



    }


    @Override
    public void hearShake() {
        Toast.makeText(getApplicationContext(), "shaking on hear", Toast.LENGTH_SHORT).show();

        if (player1Option.equals("") && player1Number.equals(user.getPhoneNumber())){
            //generate option
            Toast.makeText(getApplicationContext(), "shaking on hear2", Toast.LENGTH_SHORT).show();


            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(4);
            String option = arrayStrings.get(randomInt);

            DocumentReference optionUpdate = db.collection("games").document(docNumber);

            Map<String, Object> player = new HashMap<>();
            player.put("player1Number", player1Number);
            player.put("player1Option", option);
            // player.put("name", name);
            player.put("player2Number", player2Number);
            player.put("player2Option", player2Option);
            player.put("winner ","");

//            optionUpdate.update("player1Option", option)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "DocumentSnapshot successfully updated!");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error updating document", e);
//                        }
//                    });


        }else if(player2Option.equals("") && player2Number.equals(user.getPhoneNumber())){
            //generate option
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(4);
            String option = arrayStrings.get(randomInt);
            DocumentReference optionUpdate = db.collection("games").document(docNumber);

            optionUpdate
                    .update("player2Option", option)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

            gameLogic();
            //hearShake();
        }

    }
}

