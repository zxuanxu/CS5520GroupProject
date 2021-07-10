package edu.neu.madcourse.sticktothem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.sticktothem.Model.StickerReceiverPair;
import edu.neu.madcourse.sticktothem.Model.StickerReceiverPairAdapter;
import edu.neu.madcourse.sticktothem.Model.User;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    User user;

    Button btnSendDialog, btnHistory;
    TextView username;
    EditText receiver;
    String message;
    int numOfStickerSent;
    TextView tvNumOfStickerSent;

    // set up for RecyclerView
    ArrayList<StickerReceiverPair> stickerReceiverPairArrayList = new ArrayList<>();
    RecyclerView rvStickerReceiverPair;
    StickerReceiverPairAdapter adapter;

    public static final String SERVER_KEY = "key=AAAAH4KqPrw:APA91bE_nXgwLZvPG7s_H0OSALdP-EWb_sNE-CVmBHTUuoQLiouwCUoNMOhKo32GEAGKQneB7tOeydBVSPs1QWWf2P6OjNJqu61o77V3ZGXjKVohay1GYs09vaCSpZ3VtQwmC3TuTWzg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        tvNumOfStickerSent = findViewById(R.id.tvNumOfStickerSent);

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
                        username.setText(user.getUsername());

                        // get number of sticker sent
                        numOfStickerSent = user.getNumOfStickersSent();
                        tvNumOfStickerSent.setText(tvNumOfStickerSent.getText().toString() + numOfStickerSent + " stickers");

                    }
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // get current user's username
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    user = snapshot.getValue(User.class);
//                    username.setText(user.getUsername());
//                } catch(Exception e) {
//                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        btnSendDialog = findViewById(R.id.btnSendDialog);
        btnHistory = findViewById(R.id.btnHistory);

        // set up button to pop up a dialog to send stickers
        btnSendDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendStickerDialog();

            }
        });

        // set up RecyclerView
        rvStickerReceiverPair = (RecyclerView) findViewById(R.id.rvStickersReceived);
    }

    private void showSendStickerDialog() {
        // create an AlertDialog builder
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//
//        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
//        final View inputView = layoutInflater.inflate(R.layout.input_dialog_layout, null);
//
//        alertDialogBuilder.setView(inputView);
//
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        StickerDialog stickerDialog = new StickerDialog(
                this,
                adapter,
                stickerReceiverPairArrayList,
                user);

        stickerDialog.show(getSupportFragmentManager(), "sticker dialog");

        // find receiver's userid
//        Intent intent = getIntent();
//        String userid = intent.getStringExtra("userid");
//        String userid = "Mei";
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

//        // set onClickListener for send message button
//        Button btnSendMessage = inputView.findViewById(R.id.btnSendMessage);
//        btnSendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // find receiver's name
//                receiver = inputView.findViewById(R.id.receiver);
//                String txtReceiver = receiver.getText().toString();
//
//                if (TextUtils.isEmpty(txtReceiver)) {
//                    // if link name is empty, pop up a snack bar
//                    Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
//                } else if (TextUtils.isEmpty(message)) {
//                    // if sticker is not selected
//                    Toast.makeText(MainActivity.this, "Please select one sticker.", Toast.LENGTH_SHORT).show();
//                } else {
//                    // check if receiver exists
//                    databaseReference.child("users").child(txtReceiver)
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.getValue() != null) {
//                                        // create a new StickerSenderPair
//                                        StickerReceiverPair stickerReceiverPair = new StickerReceiverPair(message, txtReceiver);
//                                        // add newly created StickerSenderPair to list
//                                        int listCount = adapter.getItemCount();
//                                        stickerReceiverPairArrayList.add(listCount, stickerReceiverPair);
//                                        adapter.notifyItemInserted(listCount);
//
//                                        user.numOfStickersSent++;
//                                        sendSticker(firebaseUser.getUid(), stickerReceiverPair);
//                                        // close dialog
//                                        alertDialog.dismiss();
//                                        // pop message showing sticker sent successfully
//                                        Toast.makeText(MainActivity.this, "Sticker sent successfully.", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        // pop message showing receiver does not exist
//                                        Toast.makeText(MainActivity.this, "Username does not exist.", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                }
//            }
//        });

//        Button btnCancel = inputView.findViewById(R.id.btnClose);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.cancel();
//            }
//        });

    }

    private void showStickerHistory() {
        // create an AlertDialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View inputView = layoutInflater.inflate(R.layout.sticker_history_layout, null);

        alertDialogBuilder.setView(inputView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertDialog.show();

        // find current user and database reference
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").getParent();

        readReceiveStickerHistory(firebaseUser.getUid());

        Button btnClose = inputView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

//    private void sendSticker(String sender, StickerReceiverPair stickerReceiverPair) {
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        HashMap<String, Object> hashMap  = new HashMap<>();
//        hashMap.put("sender", sender);
//        hashMap.put("stickerSenderPair", stickerReceiverPair);
//
//        databaseReference.child("chats").push().setValue(hashMap);
//    }

    private View.OnClickListener stickerButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSmile:
                    message = "🙂";
                    break;
                case R.id.btnCry:
                    message = "😭";
                    break;
                case R.id.btnSad:
                    message = "🙁";
                    break;
                case R.id.btnLaugh:
                    message = "😀";
                    break;
            }
        }
    };

    private void readReceiveStickerHistory(String userid) {
        stickerReceiverPairArrayList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stickerReceiverPairArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    StickerReceiverPair stickerReceiverPair = dataSnapshot.getValue(StickerReceiverPair.class);
                    if (stickerReceiverPair.getReceiver().equals(userid)) {
                        stickerReceiverPairArrayList.add(stickerReceiverPair);
                    }
                }
                // create adapter based on current StickerReceiverPairArrayList
                adapter = new StickerReceiverPairAdapter(stickerReceiverPairArrayList);
                rvStickerReceiverPair.setAdapter(adapter);
                // set layout manager to position the items
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                rvStickerReceiverPair.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

//    private void getToken(String message, String userId) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String token = snapshot.child("token").getValue().toString();
//                String name = snapshot.child("username").getValue().toString();
//
//                // FCM use json format to send data message
//                JSONObject to = new JSONObject();
//                JSONObject data = new JSONObject();
//                try {
//                    data.put("title", name);
//                    data.put("message", message);
//                    data.put("userId", userId);
//
//                    to.put("to", token);
//                    to.put("data", data);
//
//                    sendNotification(to);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void sendNotification(JSONObject to) {
//        // url to send POST request
//        String notificationUrl = "http://fcm.googleapis.com/fcm/send";
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, notificationUrl, to, response -> {
//            Log.d("notification", "sendNotification: " + response);
//        }, error -> {
//            Log.d("notification", "sendNotification: " + error);
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                // add project server key and application type
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("Authorization", "key=" + SERVER_KEY);
//                hashMap.put("Content-Type", "application/json");
//                return hashMap;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        request.setRetryPolicy(new DefaultRetryPolicy(30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(request);
//    }

}