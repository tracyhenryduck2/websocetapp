package com.siterwell.demo.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.siterwell.demo.common.DataUtils;
import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.storage.WifiTimerDao;
import com.siterwell.demo.wheelwidget.view.WheelView;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.WIFISocketListener;
import com.siterwell.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.protocol.ResolveTimer;
import com.siterwell.sdk.protocol.SocketCommand;
import com.siterwell.sdk.protocol.ByteUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gc-0001 on 2017/6/15.
 */

public class AddTimerActivity extends TopbarSuperActivity implements WIFISocketListener{
    private final String TAG = "AddTimerActivity";
    private GridView gridView;
    private MyweekAdapter myweekAdapter;
    private String timerid,devid;
    private WifiTimerBean wifiTimerBean;
    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private ArrayList<String> actions = new ArrayList<>();
    private ArrayList<String> notices = new ArrayList<>();
    private WifiTimerDao wifiTimerDao;
    private DeviceDao deviceDao;
    private WheelView wheelView_hour,wheelView_min,wheelView_action,wheelView_notice;
    private byte wekint;
    private int confirmNum;
    private String repeatInfo;
    private SocketCommand socketCommand;
    @Override
    protected void onCreateInit() {
        SitewellSDK.getInstance(this).addWifiSocketListener(this);
        initData();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_timer;
    }

    private void initView(){
        timerid = getIntent().getStringExtra("timerid");
        String hint_txt = TextUtils.isEmpty(timerid)?getResources().getString(R.string.add_timer):getResources().getString(R.string.edit_timer);
        getTopBarView().setTopBarStatus(R.drawable.back, getResources().getString(R.string.save), hint_txt, 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmNum ++;
                verfication();
                if (confirmNum==1){
                    confirmToSys();
                }
                else if(confirmNum==-1){
                    Toast.makeText(AddTimerActivity.this,getResources().getString(R.string.set_weekday),Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }else if(confirmNum == -2){
                    Toast.makeText(AddTimerActivity.this,repeatInfo,Toast.LENGTH_LONG).show();
                    confirmNum = 0;
                }
            }
        },R.color.bar_bg);


        wheelView_hour = (WheelView)findViewById(R.id.hour);
        wheelView_min  = (WheelView)findViewById(R.id.min);
        wheelView_action = (WheelView)findViewById(R.id.action);
        wheelView_notice = (WheelView)findViewById(R.id.notice);
        gridView = (GridView)findViewById(R.id.weeklist);
        wheelView_hour.setLabel(":");
        wheelView_hour.setAdapter(new NumberAdapter(items_hour,30));
        wheelView_min.setAdapter(new NumberAdapter(items_min,30));
        wheelView_action.setAdapter(new NumberAdapter(actions,30));
        wheelView_notice.setAdapter(new NumberAdapter(notices,30));
        if(TextUtils.isEmpty(wifiTimerBean.getWeek())){
            wekint = 0x00;
            myweekAdapter = new MyweekAdapter(this,wekint);
        }else{
            wekint = ByteUtil.hexStr2Bytes(wifiTimerBean.getWeek())[0];
            myweekAdapter = new MyweekAdapter(this,wekint);
        }
        gridView.setAdapter(myweekAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myweekAdapter.isSelected.put(position,!myweekAdapter.isSelected.get(position));
                myweekAdapter.notifyDataSetChanged();
            }
        });


        wheelView_hour.setCurrentItem(wifiTimerBean.getHour());
        wheelView_min.setCurrentItem(wifiTimerBean.getMin());
        wheelView_action.setCurrentItem(wifiTimerBean.getTostatus());
        wheelView_notice.setCurrentItem(wifiTimerBean.getNotice());
    }


    private void verfication(){
        String ds = getTimerStringFromContent(myweekAdapter.isSelected);
        if("00".equals(ds)){

            confirmNum = -1;
            return;
        }


        int hour = wheelView_hour.getCurrentItem();
        int min  = wheelView_min.getCurrentItem();

        if(TextUtils.isEmpty(timerid)){
            List<String> weekList = wifiTimerDao.findAllTimerByTime(hour,min);

            HashMap<Integer,Boolean> result = CheckRepeat(weekList);
            Log.i(TAG,"result:"+result.toString());
            boolean flagrepeat = false;
            HashMap<Integer,Boolean> result2 = new HashMap<Integer,Boolean>();
            for(int i=0;i<7;i++){
                if(myweekAdapter.isSelected.get(i) && result.get(i)){
                    flagrepeat = true;
                    result2.put(i,true);
                }else {
                    result2.put(i,false);
                }
            }
            if(flagrepeat){
                repeatInfo = DataUtils.getWeekinfoHash(result2,this);
                confirmNum = -2;

                return;
            }
        }





        if(TextUtils.isEmpty(timerid)){
            wifiTimerBean.setTimerid(String.valueOf(gettid()));
            wifiTimerBean.setEnable(1);
        }

        wifiTimerBean.setTostatus(wheelView_action.getCurrentItem());
        wifiTimerBean.setWeek(ds);
        wifiTimerBean.setHour(hour);
        wifiTimerBean.setMin(min);
        wifiTimerBean.setNotice(wheelView_notice.getCurrentItem());

    }

    private void initData() {
        deviceDao       = new DeviceDao(this);
        wifiTimerDao    = new WifiTimerDao(this);
        for (int i = 0; i < 24; i ++) {

            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_hour.add(item);
        }

        for (int i = 0; i < 60; i ++) {
            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_min.add(item);
        }

        actions.add(getResources().getString(R.string.off));
        actions.add(getResources().getString(R.string.on));
        notices.add(getResources().getString(R.string.no_notice));
        notices.add(getResources().getString(R.string.notice));

        devid   = getIntent().getStringExtra("deviceid");
        timerid = getIntent().getStringExtra("timerid");
        if(TextUtils.isEmpty(timerid)){
            wifiTimerBean = new WifiTimerBean();
        }else{
            wifiTimerBean = wifiTimerDao.findTimerByTid(devid,timerid);
        }
        DeviceBean deviceBean = deviceDao.findDeviceBySid(devid);
        socketCommand = new SocketCommand(deviceBean,this);
    }

    @Override
    public void switchSocketSuccess(SocketBean socketBean) {

    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {

    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {

    }

    @Override
    public void deviceOffLineError() {

    }


    @Override
    public void refreshSocketStatus(SocketBean socketBean) {

    }

    @Override
    public void setCircleConfigSuccess(SocketBean socketBean) {

    }

    @Override
    public void setCountDownConfigSuccess(SocketBean socketBean) {

    }

    @Override
    public void setTimerConfigSuccess(WifiTimerBean wifiTimerBean) {
          wifiTimerDao.insertWifiTimer(wifiTimerBean);
          finish();
    }

    @Override
    public void deleteTimerSuccess(String id) {

    }

    @Override
    public void circleFinish(SocketBean socketBean) {

    }

    @Override
    public void countdownFinish(SocketBean socketBean) {

    }

    @Override
    public void timerFinish(SocketBean socketBean, String timerid) {

    }

    @Override
    public void unknowError() {

    }


    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items, int lengh) {
            super(items,lengh);
        }

    }

    private class MyweekAdapter extends BaseAdapter {

        private Context mcontext;
        private final String[] weekstr= getResources().getStringArray(R.array.week);
        private ViewHolder holder;
        public HashMap<Integer, Boolean> isSelected;

        public MyweekAdapter(Context mc,byte ds) {
            this.mcontext = mc;
            init(ds);
        }


        private void init(byte ds) {
            byte f;
            isSelected = new HashMap<Integer, Boolean>();

            for (int i = 0; i < weekstr.length; i++) {
                isSelected.put(i,false);
            }

            for(int i=0;i<weekstr.length;i++){
                f =   (byte)((0x02 << i) & ds);
                if(f!=0){
                    isSelected.put(i,true);
                }
            }

        }


        @Override
        public int getCount() {
            return weekstr.length;
        }

        @Override
        public String getItem(int position) {
            return weekstr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if( convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.timer_week_item,null);
                holder.textView = (TextView) convertView.findViewById(R.id.wek_item);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(weekstr[position]);
            updataBackground(position,holder.textView);
            return convertView;
        }

        @SuppressLint("NewApi")
        protected void updataBackground(int position, TextView view){

            if(isSelected.get(position)){
                view.setTextColor(mcontext.getResources().getColor(R.color.item_is_sel));
            }else{
                view.setTextColor(mcontext.getResources().getColor(R.color.item_not));
            }

        };
        private class ViewHolder {
            TextView textView;
        }

    }

    private String getTimerStringFromContent (@NotNull HashMap<Integer,Boolean> weeklist){
        byte f = 0x00;
        for(int i=0;i<weeklist.size();i++){
            if(weeklist.get(i)){
                f =   (byte)((0x02 << i) | f);
            }
        }
        String wek = ByteUtil.convertByte2HexString(f);

        return wek;
    }


    private int gettid(){

        List<String> list = wifiTimerDao.findAllTimerTid(devid);
        if(list.size()==0){
            return 0;
        }else{

            if(list.size()==1){
                if("0".equals(list.get(0))){
                    return 1;
                }else {
                    return 0;
                }

            }else{
                int m = 0;
                for(int i=0;i<list.size()-1;i++){

                    if(i==0){
                        int d = Integer.parseInt(list.get(i));
                        if(d!=0) {
                            m = 0;
                            break;
                        }
                        else {
                            if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                                m = Integer.parseInt(list.get(i))+1;
                                break;
                            }else{
                                m = i+2;
                            }
                        }
                    }else{
                        if( (Integer.parseInt(list.get(i))+1) < Integer.parseInt(list.get(i+1))){
                            m = Integer.parseInt(list.get(i))+1;
                            break;
                        }else{
                            m = i+2;
                        }
                    }


                }
                return m;
            }


        }


    }

    private HashMap<Integer, Boolean> CheckRepeat(List<String> weeklist){
        HashMap<Integer, Boolean>  isSelected = new HashMap<Integer, Boolean>();
        for (int j = 0; j < 7; j++) {
            isSelected.put(j,false);
        }

        for(int i=0;i<weeklist.size();i++){

            byte weekbtye= ByteUtil.hexStr2Bytes(weeklist.get(i))[0];

            byte f;
            for(int j=0;j<7;j++){
                f =   (byte)((0x02 << j) & weekbtye);
                if(f!=0){
                    isSelected.put(j,true);
                }
            }



        }

        return isSelected;

    }

    private void confirmToSys() {


       String messagecode = ResolveTimer.getCode(wifiTimerBean);

        Log.i(TAG,"code++++++++++:"+messagecode);

        socketCommand.setTimerInfo(messagecode,null);
        confirmNum = 0;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this).removeWifiSocketListener(this);
    }


}
