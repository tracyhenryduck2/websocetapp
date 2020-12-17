package com.siterwell.demo.device;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.siterwell.demo.R;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.device.bean.SocketDescBean;
import com.siterwell.demo.device.bean.SocketHistoryBean;
import com.siterwell.sdk.http.UserAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/9/14.
 */

public class SocketHistoryActivity extends TopbarSuperActivity {
    private final String TAG = "SocketHistoryActivity";
    private RecyclerView recyclerView;
    private SocketHistoryAdapter socketHistoryAdapter;
    private List<SocketHistoryBean> socketHistoryBeanList;
    private GridLayoutManager mLayoutManager;
    int lastVisibleItem;
    int page = 0;
    boolean isLoading = false;//用来控制进入getdata()的次数
    private SocketDescBean socketDescBean;
    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history_socket;
    }

    private void initView(){
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.history), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null, R.color.bar_bg);
        socketDescBean = (SocketDescBean) getIntent().getSerializableExtra("socketbean");
        if(socketDescBean == null){
            finish();
            return;
        }
        recyclerView = (RecyclerView)findViewById(R.id.historylist);
        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        socketHistoryBeanList = new ArrayList<>();
        socketHistoryAdapter = new SocketHistoryAdapter(this,socketHistoryBeanList);
        recyclerView.setAdapter(socketHistoryAdapter);
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == socketHistoryAdapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                    Log.e("duanlian", "onScrollStateChanged: " + "进来了");
                    socketHistoryAdapter.changeState(1);
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
        if(socketHistoryBeanList.size()==0){
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

        UserAction.getInstance(this).getAlarmHistory(page, 20, socketDescBean, new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG,object.toString());
                isLoading = false;
                try {
                    JSONObject content = new JSONObject(object.toString());
                    JSONArray list = content.getJSONArray("content");
                    int pageload = content.getInt("number");
                    if(pageload == 0) socketHistoryBeanList.clear();
                    boolean last = content.getBoolean("last");
                    if(!last) page = pageload + 1;
                    for(int i=0;i<list.length();i++){
                        SocketHistoryBean warningHistoryBean = new SocketHistoryBean();
                        warningHistoryBean.setWarningsubject(list.getJSONObject(i).getString("subject"));
                        warningHistoryBean.setReportTime(list.getJSONObject(i).getLong("reportTime"));
                        JSONObject J = list.getJSONObject(i).getJSONObject("sender");
                        warningHistoryBean.setDeviceid(J.getString("devTid"));
                        socketHistoryBeanList.add(warningHistoryBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socketHistoryAdapter.Refresh(socketHistoryBeanList);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout_em.setRefreshing(false);
                if(socketHistoryBeanList.size()==0){
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
                socketHistoryAdapter.changeState(0);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout_em.setRefreshing(false);
                Toast.makeText(SocketHistoryActivity.this, Errcode.errorCode2Msg(SocketHistoryActivity.this,errorCode),Toast.LENGTH_LONG).show();
            }
        });

    }
}
