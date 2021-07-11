package edu.neu.madcourse.sticktothem;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.neu.madcourse.sticktothem.Model.StickerSenderPair;
import edu.neu.madcourse.sticktothem.Model.StickerSenderPairAdapter;
import edu.neu.madcourse.sticktothem.Model.User;

public class StickerDialog extends AppCompatDialogFragment {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    User user;

    String sender;
    String receiver;
    String receiverToken;

    TextView username;
    EditText etReceiver;
    private String message;

    Context context;

    StickerSenderPairAdapter adapter;
    ArrayList<StickerSenderPair> stickerSenderPairArrayList;

    public StickerDialog(
            Context context,
            StickerSenderPairAdapter adapter,
            ArrayList<StickerSenderPair> stickerSenderPairArrayList,
            User user
    ) {
        this.context = context;
        this.adapter = adapter;
        this.stickerSenderPairArrayList = stickerSenderPairArrayList;
        this.user = user;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_dialog_layout, null);

        builder.setView(view);

        // set user to current user
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference
                .child("Users")
                .orderByChild("id")
                .equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // set sender
        sender = user.getUsername();


        // set onClickListener for four sticker buttons
        Button btnSmile = view.findViewById(R.id.btnSmile);
        Button btnCry = view.findViewById(R.id.btnCry);
        Button btnSad = view.findViewById(R.id.btnSad);
        Button btnLaugh = view.findViewById(R.id.btnLaugh);

        btnSmile.setOnClickListener(stickerButtonClickListener);
        btnCry.setOnClickListener(stickerButtonClickListener);
        btnLaugh.setOnClickListener(stickerButtonClickListener);
        btnSad.setOnClickListener(stickerButtonClickListener);

        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // set onClickListener for send message button
        Button btnSendMessage = view.findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find receiver's name
                etReceiver = view.findViewById(R.id.receiver);
                receiver = etReceiver.getText().toString();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference("Users");


                if (TextUtils.isEmpty(receiver)) {
                    // if link name is empty, pop up a snack bar
                    Toast.makeText(context, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(message)) {
                    // if sticker is not selected
                    Toast.makeText(context, "Please select one sticker.", Toast.LENGTH_SHORT).show();
                } else {
                    // check if receiver exists
                    databaseReference
                            .child(receiver)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // create a new StickerSenderPair
                                        StickerSenderPair stickerSenderPair = new StickerSenderPair(
                                                message, user.getUsername()
                                        );
                                        // add newly created StickerSenderPair to list
//                                        int listCount = adapter.getItemCount();
//                                        stickerReceiverPairArrayList.add(listCount, stickerReceiverPair);
//                                        adapter.notifyItemInserted(listCount);

                                        user.numOfStickersSent++;
                                        databaseReference.child(user.getUsername())
                                                .child("numOfStickersSent").setValue(user.numOfStickersSent);
                                        receiverToken = snapshot.child("token").getValue(String.class);
                                        sendSticker(receiver, stickerSenderPair);

                                        // close dialog
                                        dismiss();
                                        // pop message showing sticker sent successfully
                                        Toast.makeText(context, "Sticker sent successfully.", Toast.LENGTH_LONG).show();
                                    } else {
                                        // pop message showing receiver does not exist
                                        Toast.makeText(context, "Username does not exist.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });


        return builder.create();
    }

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

    private void sendSticker(String receiver, StickerSenderPair stickerSenderPair) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(receiver);

        HashMap<String, Object> hashMap  = new HashMap<>();
        hashMap.put("sender", stickerSenderPair.getSender());
        hashMap.put("sticker", stickerSenderPair.getSticker());
        hashMap.put("stickerSenderPair", stickerSenderPair);

        databaseReference.child("chats").push().setValue(hashMap);
        sendStickerToDevice(sender, receiverToken, message);
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
//                hashMap.put("Authorization", "key=" + MainActivity.SERVER_KEY);
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
//        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
//        request.setRetryPolicy(new DefaultRetryPolicy(30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(request);
//    }

    private void sendStickerToDevice(String sender, String receiverToken, final String sticker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                JSONObject jData = new JSONObject();

                try {
                    jNotification.put("title", "New Sticker from " + sender);
                    jNotification.put("body", sticker);
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");

                    jData.put("title", "New Sticker from " + sender);
                    jData.put("content", sticker);

                    jPayload.put("to", receiverToken);
                    jPayload.put("priority", "high");
                    jPayload.put("notification", jNotification);
                    jPayload.put("data", jData);

                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", MainActivity.SERVER_KEY);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(jPayload.toString().getBytes());
                    outputStream.close();

                    InputStream inputStream = connection.getInputStream();
                    final String response = convertStreamToString(inputStream);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(MainActivity.class.getSimpleName(), "run: " + response);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Helper function
     *
     * @param inputStream
     * @return
     */
    private String convertStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next().replace(",", ",\n") : "";
    }
}
