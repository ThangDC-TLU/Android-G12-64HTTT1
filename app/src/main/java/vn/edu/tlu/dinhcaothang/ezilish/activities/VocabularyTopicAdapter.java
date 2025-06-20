package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class VocabularyTopicAdapter extends RecyclerView.Adapter<VocabularyTopicAdapter.TopicViewHolder> {
    private List<VocabularyTopic> topicList;

    public VocabularyTopicAdapter(List<VocabularyTopic> topicList) {
        this.topicList = topicList;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        VocabularyTopic topic = topicList.get(position);
        holder.tvTopicName.setText(topic.getName());
        holder.tvWordCount.setText("(" + topic.getWordCount() + ")");
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, tvWordCount;
        CardView cardView;

        public TopicViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvWordCount = itemView.findViewById(R.id.tvWordCount);
            cardView = itemView.findViewById(R.id.cardViewTopic);
        }
    }
}