package com.siterwell.demo;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.device.bean.WarningHistoryBean;
import com.siterwell.sdk.http.UserAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gc-0001 on 2017/5/5.
 */

public class AlarmListActivity extends TopbarSuperActivity {
    private final String TAG = "AlarmList";
    private RecyclerView recyclerView;
    private AlarmHistoryAdapter alarmHistoryAdapter;
    private List<WarningHistoryBean> warnslist;
    private GridLayoutManager mLayoutManager;
    int lastVisibleItem;
    int page = 0;
    boolean isLoading = false;//用来控制进入getdata()的次数
    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm_total;
    }

    private void initView(){
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.warning_notices), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null, R.color.bar_bg);
        recyclerView = (RecyclerView)findViewById(R.id.alarmlist);
        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        warnslist = new ArrayList<>();
        alarmHistoryAdapter = new AlarmHistoryAdapter(this,warnslist);
        recyclerView.setAdapter(alarmHistoryAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                getUserallalarms();
            }
        });
        swipeRefreshLayout_em.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                getUserallalarms();
            }
        });


        //给recyclerView添加滑动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
        /*
        到达底部了,如果不加!isLoading的话到达底部如果还一滑动的话就会一直进入这个方法
        就一直去做请求网络的操作,这样的用户体验肯定不好.添加一个判断,每次滑倒底只进行一次网络请求去请求数据
        当请求完成后,在把isLoading赋值为false,下次滑倒底又能进入这个方法了
         */
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == alarmHistoryAdapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                        Log.e("duanlian", "onScrollStateChanged: " + "进来了");
                        alarmHistoryAdapter.changeState(1);
                        getUserallalarms();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(warnslist.size()==0){
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout_em.setVisibility(View.VISIBLE);
        }else{
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout_em.setVisibility(View.GONE);
        }
        page = 0;
        getUserallalarms();
    }

    private void getUserallalarms(){

        if(isLoading) return;

        isLoading = true;

        if(page ==0){
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout_em.setRefreshing(true);
        }


        UserAction.getInstance(this).getAllAlarmHistory(page, 20, new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG,object.toString());
                isLoading = false;
                try {
                    JSONObject content = new JSONObject(object.toString());
                    JSONArray list = content.getJSONArray("content");
                    int pageload = content.getInt("number");
                    if(pageload == 0) warnslist.clear();
                    boolean last = content.getBoolean("last");
                    if(!last) page = pageload + 1;
                    for(int i=0;i<list.length();i++){
                        WarningHistoryBean warningHistoryBean = new WarningHistoryBean();
                        warningHistoryBean.setWarningsubject(list.getJSONObject(i).getString("subject"));
                        warningHistoryBean.setReportTime(list.getJSONObject(i).getLong("reportTime"));
                        JSONObject J = list.getJSONObject(i).getJSONObject("sender");
                        warningHistoryBean.setDeviceid(J.getString("devTid"));
                        warnslist.add(warningHistoryBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alarmHistoryAdapter.Refresh(warnslist);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout_em.setRefreshing(false);
                if(warnslist.size()==0){
                    swipeRefreshLayout.setVisibility(View.GONE);
                    swipeRefreshLayout_em.setVisibility(View.VISIBLE);
                }else{
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout_em.setVisibility(View.GONE);
                }
            }

            @Override
            public void getFail(int errorCode) {
                isLoading = false;
                alarmHistoryAdapter.changeState(0);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout_em.setRefreshing(false);
                Toast.makeText(AlarmListActivity.this, Errcode.errorCode2Msg(AlarmListActivity.this,errorCode),Toast.LENGTH_LONG).show();
            }
        });

    }
}
