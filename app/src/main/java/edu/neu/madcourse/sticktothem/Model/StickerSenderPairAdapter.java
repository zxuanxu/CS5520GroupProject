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


public class StickerSenderPairAdapter extends RecyclerView.Adapter<StickerSenderPairAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sticker;
        public TextView sender;

        public ViewHolder(View itemView) {
            super(itemView);
            sticker = itemView.findViewById(R.id.tvStickerReceived);
            sender = itemView.findViewById(R.id.tvStickerSender);
        }
    }

    private List<StickerSenderPair> stickerSenderPairList;

    public StickerSenderPairAdapter(List<StickerSenderPair> stickerSenderPairList) {
        this.stickerSenderPairList = stickerSenderPairList;
    }

    @Override
    public StickerSenderPairAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View stickerSenderPairItemView = inflater.inflate(R.layout.item_sticker, parent, false);

        // Return a new holder instance
        StickerSenderPairAdapter.ViewHolder viewHolder = new ViewHolder(stickerSenderPairItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StickerSenderPairAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        StickerSenderPair stickerSenderPair = stickerSenderPairList.get(position);

        // Set item views based on views and data model
        holder.sticker.setText(stickerSenderPair.getSticker());
        holder.sender.setText(stickerSenderPair.getSender());
    }

    @Override
    public int getItemCount() {
        return stickerSenderPairList.size();
    }
}
