package vn.edu.tlu.dinhcaothang.ezilish.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.activities.WordListActivity;
import vn.edu.tlu.dinhcaothang.ezilish.models.VocabularyTopic;

public class VocabularyTopicAdapter extends RecyclerView.Adapter<VocabularyTopicAdapter.TopicViewHolder> {
    private List<VocabularyTopic> topicList;
    private Context context;

    // Thêm constructor có Context
    public VocabularyTopicAdapter(List<VocabularyTopic> topicList, Context context) {
        this.topicList = topicList;
        this.context = context;
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
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WordListActivity.class);
            intent.putExtra("topicId", topic.getId());
            intent.putExtra("topicName", topic.getName());
            context.startActivity(intent);
        });
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