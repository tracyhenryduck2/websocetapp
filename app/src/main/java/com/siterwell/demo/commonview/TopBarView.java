package com.siterwell.demo.commonview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siterwell.demo.R;
import com.siterwell.demo.common.LableTextWatcher;


/**
 * TODO: document your custom view class.
 */
public class TopBarView extends RelativeLayout implements View.OnClickListener{

    private Context mContext ;

    private RelativeLayout backgroud;
    private ImageView btn_back;
    private TextView txt_title;
    private EditText edit_title;
    private TextView txt_left;
    private ImageView img_setting;//右上角图标按钮
    private TextView txt_setting;//右上角文字按钮
    private LinearLayout edit_title_lay;
    private ImageView del_img;  //清除输入内容按钮
    private int left_type = 1;   //1代表返回箭头，2代表文字
    private int title_type=1;    //1代表textview显示,2代表editview显示
    private int setting_type=1;  //1代表图标按钮显示,2代表文字按钮显示,3代表都不显示


    public TopBarView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.top_bar, this, true);
        del_img =(ImageView)findViewById(R.id.del_name);
        btn_back = (ImageView) findViewById(R.id.left_img);
        txt_title = (TextView) findViewById(R.id.title);
        edit_title = (EditText) findViewById(R.id.title_edit);
        txt_left = (TextView)findViewById(R.id.left_txt);
        img_setting = (ImageView) findViewById(R.id.right_img);
        txt_setting = (TextView) findViewById(R.id.right_txt);
        backgroud = (RelativeLayout)findViewById(R.id.toolbar);
        edit_title_lay =(LinearLayout)findViewById(R.id.title_edit_lay);
        del_img.setOnClickListener(this);
        edit_title.addTextChangedListener(new LableTextWatcher(del_img));
    }


    public void setTopBarStatus(int Res_left , int Res_right, String hint_text ,int type, View.OnClickListener onClickListener1, View.OnClickListener onClickListener2, int  Bgresid_color){
         if(backgroud!=null){
             backgroud.setBackgroundColor(getResources().getColor(Bgresid_color));
         }
        setTxt_titleType(type,hint_text);

        txt_left.setVisibility(View.GONE);
        txt_setting.setVisibility(View.GONE);

        if(Res_left==-1){
            btn_back.setVisibility(View.GONE);
        }else{
            btn_back.setVisibility(View.VISIBLE);
            btn_back.setImageResource(Res_left);
        }
        if(Res_right==-1){
            img_setting.setVisibility(View.GONE);
        }else{
            img_setting.setVisibility(View.VISIBLE);
            img_setting.setImageResource(Res_right);
        }
        btn_back.setOnClickListener(onClickListener1);
        img_setting.setOnClickListener(onClickListener2);

    }


    public void setTopBarStatus(int Res_left , String txt_right, String hint_text ,int type, View.OnClickListener onClickListener1, View.OnClickListener onClickListener2, int  Bgresid_color){
        if(backgroud!=null){
            backgroud.setBackgroundColor(getResources().getColor(Bgresid_color));
        }
        setTxt_titleType(type,hint_text);

        txt_left.setVisibility(View.GONE);
        img_setting.setVisibility(View.GONE);

        if(Res_left==-1){
            btn_back.setVisibility(View.GONE);
        }else{
            btn_back.setVisibility(View.VISIBLE);
            btn_back.setImageResource(Res_left);
        }

        if(TextUtils.isEmpty(txt_right)){
            txt_setting.setVisibility(View.GONE);
        }else{
            txt_setting.setVisibility(View.VISIBLE);
            txt_setting.setText(txt_right);
        }
        btn_back.setOnClickListener(onClickListener1);
        txt_setting.setOnClickListener(onClickListener2);
    }

    public void setTopBarStatus(String tx_left , int Res_right, String hint_text ,int type, View.OnClickListener onClickListener1, View.OnClickListener onClickListener2, int  Bgresid_color){
        if(backgroud!=null){
            backgroud.setBackgroundColor(getResources().getColor(Bgresid_color));
        }
        setTxt_titleType(type,hint_text);

        btn_back.setVisibility(View.GONE);
        txt_setting.setVisibility(View.GONE);



        if(TextUtils.isEmpty(tx_left)){
            txt_left.setVisibility(View.GONE);
        }else{
            txt_left.setVisibility(View.VISIBLE);
            txt_left.setText(tx_left);
        }

        if(Res_right==-1){
            img_setting.setVisibility(View.GONE);
        }else{
            img_setting.setVisibility(View.VISIBLE);
            img_setting.setImageResource(Res_right);
        }
        txt_left.setOnClickListener(onClickListener1);
        img_setting.setOnClickListener(onClickListener2);
    }

    public void setTopBarStatus(String tx_left ,  String txt_right, String hint_text ,int type, View.OnClickListener onClickListener1, View.OnClickListener onClickListener2, int  Bgresid_color){
        if(backgroud!=null){
            backgroud.setBackgroundColor(getResources().getColor(Bgresid_color));
        }
        setTxt_titleType(type,hint_text);


        btn_back.setVisibility(View.GONE);
        img_setting.setVisibility(View.GONE);

        if(TextUtils.isEmpty(tx_left)){
            txt_left.setVisibility(View.GONE);
        }else{
            txt_left.setVisibility(View.VISIBLE);
            txt_left.setText(tx_left);
        }
        if(TextUtils.isEmpty(txt_right)){
            txt_setting.setVisibility(View.GONE);
        }else{
            txt_setting.setVisibility(View.VISIBLE);
            txt_setting.setText(txt_right);
        }

        txt_left.setOnClickListener(onClickListener1);
        txt_setting.setOnClickListener(onClickListener2);
    }


    private void setTxt_titleType(int type,String hint_txt){
        if(type==1){
            txt_title.setVisibility(View.VISIBLE);
            edit_title_lay.setVisibility(View.GONE);
            txt_title.setText(hint_txt);
        }else if(type == 2){
            edit_title_lay.setVisibility(View.VISIBLE);
            txt_title.setVisibility(View.GONE);
            edit_title.setHint(hint_txt);
        }
    }

    public void setTextTitle(String title){
            txt_title.setText(title);
    }

    public EditText getEditTitle(){
        return edit_title;
    }

    public ImageView getDelView(){ return del_img;}

    public void setEditTitle(String title){

            edit_title.setText(title);
            edit_title.setSelection(title.length());
    }

    public void setSettingTxt(String title){
        txt_setting.setText(title);
    }

    public TextView getSettingTxt(){
        return txt_setting;
    }

    public void setTitleOnclickListener(OnClickListener l){
        txt_title.setClickable(true);
        txt_title.setOnClickListener(l);
    }

    public String getEditTitleText(){
        String str = edit_title.getText().toString().trim();

        return str;

    }

    @Override
    public void onClick(View v) {
         if(v.getId()==R.id.del_name){
             edit_title.setText("");
         }
    }

}
