package com.siterwell.demo.commonview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by gc-0001 on 2017/4/24.
 */

public class EmRecylerView extends RecyclerView {
    private static final String TAG = "EmRecylerView";

    public EmRecylerView(Context context) {
        super(context);
    }

    public EmRecylerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmRecylerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private View emptyView;


    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.i(TAG, "onItemRangeInserted" + itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };


    private void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible =
                    getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//
//        Log.i("SlidingMenu","Recyclerview+++++++++++e.getAction():"+e.getAction());
//        if(e.getAction()==MotionEvent.ACTION_DOWN){
//            return false;
//        }else{
//            boolean flag = super.onTouchEvent(e);
//
//            Log.i("SlidingMenu","Recyclerview+++++++++++flag:"+flag);
//            return flag;
//        }
//
//    }


}
