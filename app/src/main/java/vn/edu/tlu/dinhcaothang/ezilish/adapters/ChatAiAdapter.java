package vn.edu.tlu.dinhcaothang.ezilish.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.models.MessageAi;

public class ChatAiAdapter extends RecyclerView.Adapter<ChatAiAdapter.ChatViewHolder> {
    private List<MessageAi> messageAis;

    public ChatAiAdapter(List<MessageAi> messageAis) {
        this.messageAis = messageAis;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageAi msg = messageAis.get(position);
        if (msg.isUser()) {
            holder.userMessage.setText(msg.getContent());
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.botMessage.setVisibility(View.GONE);
        } else {
            holder.botMessage.setText(msg.getContent());
            holder.botMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageAis.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage, botMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.tvUserMessage);
            botMessage = itemView.findViewById(R.id.tvBotMessage);
        }
    }
}
