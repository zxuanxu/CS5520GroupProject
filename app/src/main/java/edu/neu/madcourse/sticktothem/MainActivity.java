package edu.neu.madcourse.sticktothem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;

import edu.neu.madcourse.sticktothem.Model.User;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    User user;

    Button btnSendDialog, btnHistory;
    TextView username;
    EditText receiver;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseInstallations.getInstance().getId().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

            }
        });

        btnSendDialog = findViewById(R.id.btnSendDialog);
        btnHistory = findViewById(R.id.btnHistory);

        btnSendDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendStickerDialog();
            }
        });
    }

    private void showSendStickerDialog() {
        // create an AlertDialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View inputView = layoutInflater.inflate(R.layout.input_dialog_layout, null);

        alertDialogBuilder.setView(inputView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertDialog.show();


        // find receiver's userid
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        // set onClickListener for four sticker buttons
        Button btnSmile = findViewById(R.id.btnSmile);
        Button btnCry = findViewById(R.id.btnCry);
        Button btnSad = findViewById(R.id.btnSad);
        Button btnLaugh = findViewById(R.id.btnLaugh);

        btnCry.setOnClickListener(stickerButtonListener);
        btnSmile.setOnClickListener(stickerButtonListener);
        btnLaugh.setOnClickListener(stickerButtonListener);
        btnSad.setOnClickListener(stickerButtonListener);

        // set onClickListener for send message button
        Button btnSendMessage = inputView.findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find receiver's name
                receiver = inputView.findViewById(R.id.receiver);
                String txtReceiver = receiver.getText().toString();

                if (TextUtils.isEmpty(txtReceiver)) {
                    // if link name is empty, pop up a snack bar
                    Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(message)) {
                    // if sticker is not selected
                    Toast.makeText(MainActivity.this, "Please select one sticker.", Toast.LENGTH_SHORT).show();
                } else {
                    sendSticker(firebaseUser.getUid(),userid, message);

                    // pop message showing sticker sent successfully
                    Toast.makeText(MainActivity.this, "Sticker sent successfully.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnCancel = inputView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    private void sendSticker(String sender, String receiver, String message) {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap  = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        databaseReference.child("chats").push().setValue(hashMap);
    }

    private View.OnClickListener stickerButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSmile:
                    message = "\uD83D\uDE42";
                    break;
                case R.id.btnCry:
                    message = "\uD83D\uDE2D";
                    break;
                case R.id.btnSad:
                    message = "\uD83D\uDE41";
                    break;
                case R.id.btnLaugh:
                    message = "\uD83D\uDE00";
                    break;
            }
        }
    };

}