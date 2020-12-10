package com.siterwell.demo;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.siterwell.demo.common.TopbarSuperActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc-0001 on 2017/5/28.
 */

public class QuestionActivity extends TopbarSuperActivity implements QuestionAdapter.OnRecyclerViewItemClickListener{
    private final String TAG = "QuestionActivity";
    private RecyclerView recyclerView;
    private QuestionAdapter questionAdapter;
    private List<QuestionBean> questionBeenlist;
    private GridLayoutManager mLayoutManager;
    private boolean net_help = false;

    @Override
    protected void onCreateInit() {
        initdata();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_question;
    }


    private void initView(){
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.you_ask_i_answer_new), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null, R.color.bar_bg);
        recyclerView = (RecyclerView)findViewById(R.id.questionlist);
        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        questionAdapter = new QuestionAdapter(this,questionBeenlist);
        recyclerView.setAdapter(questionAdapter);
        questionAdapter.setOnItemClickListener(this);
        if(net_help){
            mLayoutManager.scrollToPositionWithOffset(3, 0);
        }
    }

    private void initdata(){
        net_help = getIntent().getBooleanExtra("help",false);
        questionBeenlist = new ArrayList<QuestionBean>();
        String[] ques = getResources().getStringArray(R.array.question);
        String[] answer = getResources().getStringArray(R.array.answer);
        for(int i=0;i<ques.length;i++){
            QuestionBean questionBean = new QuestionBean();

            if(net_help&&i==3){
                questionBean.setIsopen(true);
            }else{
                questionBean.setIsopen(false);
            }
            questionBean.setAnswer(answer[i]);
            questionBean.setQuestion("Q"+(i+1)+"."+ques[i]);
            questionBeenlist.add(questionBean);

        }

    }

    @Override
    public void onItemClick(View view, int position) {
          if(questionBeenlist.get(position).isopen()){
              questionBeenlist.get(position).setIsopen(false);
          }else{
              questionBeenlist.get(position).setIsopen(true);
          }
        questionAdapter.Refresh(questionBeenlist);
    }
}
