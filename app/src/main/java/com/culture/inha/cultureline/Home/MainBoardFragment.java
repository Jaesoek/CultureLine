package com.culture.inha.cultureline.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.culture.inha.cultureline.Account.ProfileActivity;
import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.BoardMain.DetailBoardActivity;
import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.DataSet.Author;
import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.DataSet.QuestionSet;
import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.Lib.ActivityResultEvent;
import com.culture.inha.cultureline.Lib.EventBus;
import com.culture.inha.cultureline.Posting.PostQuestionActivity;
import com.culture.inha.cultureline.R;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;

public class MainBoardFragment extends Fragment implements MainBoardAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "MainBoardFragment";
    private static final String ARG_SECTION_NUMBER = "mainBoard";

    public static final int RC_POST_QUESTION = 80;
    public static final int RC_ELSE_QUESTION = 83;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MainBoardAdapter mAdapter;
    private FloatingActionButton fabPostQuestion;
    private ArrayList<Question> mQuestionSet;
    private SwipeRefreshLayout swipeView;

    private AppCompatTextView[] txtCategories;
    private boolean[] isCategorySet;

    ApiInterface apiInterface;
    int nowPageNum = 1;
    int nextPageNum = -1;

    public MainBoardFragment() {
    }

    public static MainBoardFragment newInstance(int sectionNumber) {
        MainBoardFragment fragment = new MainBoardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_mainboard, container, false);

        LinearLayout rootLinear = view.findViewById(R.id.layout_Category);
        isCategorySet = new boolean[GlobalDataSet.categories.size()];
        txtCategories = new AppCompatTextView[GlobalDataSet.categories.size()];
        for (int i = 0; i < GlobalDataSet.categories.size(); ++i) {
            txtCategories[i] = (AppCompatTextView) inflater.inflate(R.layout.view_category, rootLinear, false);
            rootLinear.addView(txtCategories[i]);
            txtCategories[i].setText("#" + GlobalDataSet.categories.get(i));
            txtCategories[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatTextView temp = (AppCompatTextView) view;
                    int position = 0;
                    for (int i = 0; i < txtCategories.length; ++i)
                        if (temp.getText().toString().contains(txtCategories[i].getText())) {
                            position = i;
                            break;
                        }

                    isCategorySet[position] = !isCategorySet[position];
                    if (isCategorySet[position]) {
                        txtCategories[position].setTextColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        txtCategories[position].setTextColor(getResources().getColor(R.color.colorBlack));
                    }
                    onRefresh();
                }
            });
        }

        mQuestionSet = new ArrayList<>();
        swipeView = view.findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.recycleMainBoard);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainBoardAdapter(new MainBoardAdapter.OnItemClickListener() {
            @Override
            public void onQuestionClick(Question question) {
                Intent intent = new Intent(getContext(), DetailBoardActivity.class);
                intent.putExtra("question", question);
                getActivity().startActivityForResult(intent, RC_ELSE_QUESTION);
            }

            @Override
            public void onProfileClick(Author author) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("author", author);
                startActivity(intent);
            }
        }, this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()

        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && llManager.findLastCompletelyVisibleItemPosition() == (mAdapter.getItemCount() - 2)) {
                    mAdapter.showLoading();
                }
            }
        });

        fabPostQuestion = view.findViewById(R.id.fabPostQuestion);
        fabPostQuestion.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                getActivity().startActivityForResult(new Intent(view.getContext(), PostQuestionActivity.class),
                        RC_POST_QUESTION);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        loadData();

        return view;
    }

    /**
     * 첫번쨰 페이지를 로드한다
     * 그 이후에는 호출되지 않음
     */
    private void loadData() {
        Call<QuestionSet> callLoad = apiInterface.mainboard(getActivity()
                .getSharedPreferences("user", 0).getString("token", "hello"));
        callLoad.enqueue(new Callback<QuestionSet>() {
            @Override
            public void onResponse(Call<QuestionSet> call, Response<QuestionSet> response) {
                Log.d(TAG, "Connecting api is success");
                mQuestionSet.clear();
                mQuestionSet.addAll(response.body().getData());
                mAdapter.addAll(mQuestionSet);
                if (response.body().getNextPageUrl() != null) nextPageNum = nowPageNum + 1;
            }

            @Override
            public void onFailure(Call<QuestionSet> call, Throwable t) {
                Log.d(TAG, "Connecting api is Failed");
            }
        });
    }

    /**
     * 첫번째 이후 페이지들을 로드한다
     * pageNum 값
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onLoadMore() {
        new AsyncTask<Void, Void, ArrayList<Question>>() {
            @Override
            protected ArrayList<Question> doInBackground(Void... voids) {
                if (nextPageNum == -1) {
                    return null;
                } else {
                    nowPageNum = nextPageNum;
                    Call<QuestionSet> callMore = apiInterface.mainBoardNext(getActivity()
                                    .getSharedPreferences("user", 0).getString("token", "hello"),
                            nowPageNum);
                    try {
                        Response<QuestionSet> response = callMore.execute();
                        if (response.body().getNextPageUrl() != null) nextPageNum += 1;
                        else nextPageNum = -1;

                        return (ArrayList<Question>) response.body().getData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<Question> items) {
                super.onPostExecute(items);
                if (items == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "더이상 로드할 데이터가 없습니다", Toast.LENGTH_SHORT).show();
                    mAdapter.dismissLoading();
                } else {
                    mAdapter.dismissLoading();
                    mAdapter.addItemMore(items);
                    mAdapter.setMore(true);
                }
            }
        }.execute();
    }

    /**
     * 상단 스크롤을 사용하여 다시 리스트를 불러온다
     */
    @Override
    public void onRefresh() {
        // 설정된 카테고리로 데이터를 다시 불러오기 전에 추가적인 요청 방지하기 위한 설정
        for (AppCompatTextView txtCategory : txtCategories) txtCategory.setEnabled(false);
        Call<QuestionSet> callLoad;
        if (GlobalDataSet.setCategoryName(isCategorySet) != null) {
            callLoad = apiInterface.mainBoardCategory(
                    getActivity().getSharedPreferences("user", 0).getString("token", "hello"),
                    GlobalDataSet.setCategoryName(isCategorySet));
        } else {
            callLoad = apiInterface.mainboard(
                    getActivity().getSharedPreferences("user", 0).getString("token", "hello"));
        }
        callLoad.enqueue(new Callback<QuestionSet>() {
            @Override
            public void onResponse(Call<QuestionSet> call, Response<QuestionSet> response) {
                Log.d(TAG, "Refresh Connecting api is success");

                mQuestionSet.clear();
                mQuestionSet.addAll(response.body().getData());
                mAdapter.addAll(mQuestionSet);
                swipeView.setRefreshing(false);

                nowPageNum = 1;
                if (response.body().getNextPageUrl() != null) nextPageNum = nowPageNum + 1;
                else nextPageNum = -1;
                mAdapter.setMore(true);

                // 카테고리 추가요청 허용
                for (AppCompatTextView txtCategory : txtCategories) txtCategory.setEnabled(true);
            }

            @Override
            public void onFailure(Call<QuestionSet> call, Throwable t) {
                Log.d(TAG, "Refresh Connecting api is Failed");
            }
        });
    }

    private int getQuestionPosition(int qId) {
        int result = 0;
        for (Question question : mQuestionSet) {
            if (question.getId() == qId)
                return result;
            result++;
        }
        return result;
    }


    /**
     * 질문 작성뒤에 해당 질문을 리스트에 추가하는 과정
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onActivityResultEvent(@NonNull ActivityResultEvent event) {
        onActivityResult(event.getRequestCode(), event.getResultCode(), event.getData());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Receive result response");
        // 질문 추가적으로 생성
        if (requestCode == RC_POST_QUESTION) {
            mQuestionSet.add(0, (Question) data.getSerializableExtra("question"));
            mAdapter.addItemMore(mQuestionSet.get(0));
        } else if (requestCode == RC_ELSE_QUESTION) {
            // question이 intent로 돌아오면 답변의 개수 변경(답변 삭제 및 답변 추가)
            if (data.getSerializableExtra("question") != null) {
                Question questionFromIntent = (Question) data.getSerializableExtra("question");
                mQuestionSet.set(getQuestionPosition(questionFromIntent.getId()), questionFromIntent);
                mAdapter.changeItem(getQuestionPosition(questionFromIntent.getId()), questionFromIntent);
            }
            // 질문이 삭제될 경우 intent에는 int형 questionId가 있음
            else {
                int deletedQuestionPostion = getQuestionPosition(data.getIntExtra("questionId", -1));
                mQuestionSet.remove(deletedQuestionPostion);
                mAdapter.removeItem(deletedQuestionPostion);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getInstance().unregister(this);
        super.onDestroyView();
    }
}