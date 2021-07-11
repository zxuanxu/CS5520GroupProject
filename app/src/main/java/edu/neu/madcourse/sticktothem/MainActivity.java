package edu.neu.madcourse.sticktothem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import edu.neu.madcourse.sticktothem.Model.StickerSenderPair;
import edu.neu.madcourse.sticktothem.Model.StickerSenderPairAdapter;
import edu.neu.madcourse.sticktothem.Model.User;

public class MainActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    // set up for Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    User user;
    String usernameStr;

    // set up for two dialogs
    Button btnSendDialog, btnHistory;
    TextView username;
    int numOfStickerSent;
    TextView tvNumOfStickerSent;

    // set up for RecyclerView
    ArrayList<StickerSenderPair> stickerSenderPairArrayList = new ArrayList<>();
    RecyclerView rvStickerReceiverPair;
    StickerSenderPairAdapter adapter;

    public static final String SERVER_KEY = "key=AAAAH4KqPrw:APA91bE_nXgwLZvPG7s_H0OSALdP-EWb_sNE-CVmBHTUuoQLiouwCUoNMOhKo32GEAGKQneB7tOeydBVSPs1QWWf2P6OjNJqu61o77V3ZGXjKVohay1GYs09vaCSpZ3VtQwmC3TuTWzg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        tvNumOfStickerSent = findViewById(R.id.tvNumOfStickerSent);

        // set up RecyclerView
        rvStickerReceiverPair = (RecyclerView) findViewById(R.id.rvStickersReceived);
        adapter = new StickerSenderPairAdapter(stickerSenderPairArrayList);
        rvStickerReceiverPair.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvStickerReceiverPair.setLayoutManager(layoutManager);

        // get current user and database reference
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .orderByChild("id")
                .equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        usernameStr = user.getUsername();
                        username.setText(usernameStr);

                        // get real-time number of sticker sent
                        FirebaseDatabase.getInstance()
                                .getReference(  "Users")
                                .child(user.getUsername())
                                .child("numOfStickersSent").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                numOfStickerSent = snapshot.getValue(Integer.class);
                                tvNumOfStickerSent.setText("You have sent out: " + numOfStickerSent + " stickers");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // get current user's token
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }
                                        String token = task.getResult();

                                        FirebaseDatabase
                                                .getInstance()
                                                .getReference()
                                                .child("Users")
                                                .child(user.getUsername())
                                                .child("token")
                                                .setValue(token);
                                    }
                                });

                    }
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // set up onClickListener for two buttons in MainActivity
        btnSendDialog = findViewById(R.id.btnSendDialog);
        btnHistory = findViewById(R.id.btnHistory);

        // set up button to pop up a dialog to send stickers
        btnSendDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendStickerDialog();
            }
        });

        // set up button to pop up a dialog for sticker history
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStickerHistory();
            }
        });
    }

    private void showSendStickerDialog() {
        StickerDialog stickerDialog = new StickerDialog(
                this,
                adapter,
                stickerSenderPairArrayList,
                user);

        stickerDialog.show(getSupportFragmentManager(), "sticker dialog");
    }

    private void showStickerHistory() {
        // find current user and database reference
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(usernameStr);

        // if there is no chat message
        databaseReference.orderByKey().equalTo("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "You have not received any sticker yet.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        readReceiveStickerHistory();
    }

    private void readReceiveStickerHistory() {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(usernameStr)
                .child("chats")
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stickerSenderPairArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    StickerSenderPair stickerSenderPair = dataSnapshot.getValue(StickerSenderPair.class);
                    stickerSenderPairArrayList.add(0, stickerSenderPair);
                }
                // update adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}