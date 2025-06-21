package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.os.AsyncTask;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.activities.QuizQuestion;

public class QuizGenerator {
    private static final String OPENAI_API_KEY = "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";
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public interface Callback {
        void onSuccess(List<QuizQuestion> quiz);
        void onError(String error);
    }

    public static void generateQuiz(String topic, Callback callback) {
        new AsyncTask<Void, Void, List<QuizQuestion>>() {
            String errorMsg = null;

            @Override
            protected List<QuizQuestion> doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject messageObj = new JSONObject();
                    messageObj.put("role", "user");
                    messageObj.put("content", "Generate 5 multiple-choice English quiz questions about the topic \"" + topic + "\". Each question should have 4 options (A, B, C, D) and specify the correct answer. Format your response as JSON like this:\n[\n  {\n    \"question\": \"...\",\n    \"options\": [\"...\", \"...\", \"...\", \"...\"],\n    \"answer\": \"A\"\n  }, ...]");

                    JSONArray messages = new JSONArray();
                    messages.put(messageObj);

                    JSONObject reqBody = new JSONObject();
                    reqBody.put("model", "gpt-3.5-turbo");
                    reqBody.put("messages", messages);
                    reqBody.put("temperature", 0.7);

                    RequestBody body = RequestBody.create(JSON, reqBody.toString());
                    Request request = new Request.Builder()
                            .url(ENDPOINT)
                            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String resBody = response.body().string();
                        JSONObject json = new JSONObject(resBody);
                        JSONArray choices = json.getJSONArray("choices");
                        String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
                        // content là chuỗi JSON array
                        // Đôi khi AI trả về thừa ký tự (markdown, ```), cần xử lý làm sạch:
                        content = content.replaceAll("```json", "").replaceAll("```", "").trim();

                        JSONArray arr = new JSONArray(content);
                        List<QuizQuestion> list = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject q = arr.getJSONObject(i);
                            QuizQuestion quiz = new QuizQuestion();
                            quiz.question = q.getString("question");
                            JSONArray optionsArr = q.getJSONArray("options");
                            quiz.options = new ArrayList<>();
                            for (int j = 0; j < optionsArr.length(); j++) {
                                quiz.options.add(optionsArr.getString(j));
                            }
                            quiz.answer = q.getString("answer");
                            list.add(quiz);
                        }
                        return list;
                    } else {
                        errorMsg = "API error: " + response.code();
                        return null;
                    }
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<QuizQuestion> quiz) {
                if (quiz != null) {
                    callback.onSuccess(quiz);
                } else {
                    callback.onError(errorMsg);
                }
            }
        }.execute();
    }
}
