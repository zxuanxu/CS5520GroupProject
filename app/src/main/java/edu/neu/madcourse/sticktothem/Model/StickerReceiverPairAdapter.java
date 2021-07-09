package edu.neu.madcourse.sticktothem.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.sticktothem.R;


public class StickerReceiverPairAdapter extends RecyclerView.Adapter<StickerReceiverPairAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sticker;
        public TextView receiver;

        public ViewHolder(View itemView) {
            super(itemView);
            sticker = itemView.findViewById(R.id.tvStickerReceived);
            receiver = itemView.findViewById(R.id.tvStickerReceiver);
        }
    }

    private List<StickerReceiverPair> stickerReceiverPairList;

    public StickerReceiverPairAdapter(List<StickerReceiverPair> stickerReceiverPairList) {
        this.stickerReceiverPairList = stickerReceiverPairList;
    }

    @Override
    public StickerReceiverPairAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View stickerSenderPairItemView = inflater.inflate(R.layout.item_sticker, parent, false);

        // Return a new holder instance
        StickerReceiverPairAdapter.ViewHolder viewHolder = new ViewHolder(stickerSenderPairItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StickerReceiverPairAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        StickerReceiverPair stickerReceiverPair = stickerReceiverPairList.get(position);

        // Set item views based on views and data model
        holder.sticker.setText(stickerReceiverPair.getSticker());
        holder.receiver.setText(stickerReceiverPair.getReceiver());
    }

    @Override
    public int getItemCount() {
        return stickerReceiverPairList.size();
    }
}
