package vn.edu.tlu.dinhcaothang.ezilish.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.models.Topic;
import vn.edu.tlu.dinhcaothang.ezilish.R;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private List<Topic> topics;
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.tvTopicName.setText(topic.name);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTopicClick(topic);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;
        TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
        }
    }
}