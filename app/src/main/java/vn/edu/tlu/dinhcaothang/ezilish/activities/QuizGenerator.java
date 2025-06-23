package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.os.AsyncTask;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class QuizGenerator {
    private static final String GEMINI_API_KEY = "AIzaSyDBNw-QudTYv3sOqmCYNNhdN0vD79DF5qs";
    // Sử dụng model vision cho API key lấy từ AI Studio
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public interface Callback {
        void onSuccess(List<QuizQuestion> quiz);
        void onError(String error);
    }

    public static void generateQuiz(String topic, int numQuestions, Callback callback) {
        new AsyncTask<Void, Void, List<QuizQuestion>>() {
            String errorMsg = null;

            @Override
            protected List<QuizQuestion> doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();

                    // Build prompt for Gemini, SỬA chỗ này để lấy đúng số câu hỏi!
                    String prompt = "Generate " + numQuestions + " multiple-choice English quiz questions about the topic \"" + topic + "\". "
                            + "Each question should have 4 options (A, B, C, D) and specify the correct answer. "
                            + "Format your response as JSON like this:\\n[\\n  {\\n    \"question\": \"...\",\\n    \"options\": [\"...\", \"...\", \"...\", \"...\"],\\n    \"answer\": \"A\"\\n  }, ...]";

                    JSONObject part = new JSONObject();
                    part.put("text", prompt);

                    JSONArray parts = new JSONArray();
                    parts.put(part);

                    JSONObject content = new JSONObject();
                    content.put("parts", parts);
                    JSONArray contents = new JSONArray();
                    contents.put(content);

                    JSONObject reqBody = new JSONObject();
                    reqBody.put("contents", contents);

                    RequestBody body = RequestBody.create(reqBody.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(ENDPOINT)
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String resBody = response.body().string();
                            JSONObject json = new JSONObject(resBody);
                            JSONArray candidates = json.getJSONArray("candidates");
                            String contentText = candidates.getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");
                            // Loại bỏ ký tự markdown nếu có
                            contentText = contentText.replaceAll("```json", "").replaceAll("```", "").trim();

                            JSONArray arr = new JSONArray(contentText);
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
                            String bodyStr = response.body() != null ? response.body().string() : "";
                            errorMsg = "Gemini API error: " + response.code() + " - " + bodyStr;
                            return null;
                        }
                    } finally {
                        response.close();
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