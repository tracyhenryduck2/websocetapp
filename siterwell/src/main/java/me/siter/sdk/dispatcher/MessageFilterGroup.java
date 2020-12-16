package me.siter.sdk.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * IMessageFiler的实现，包装多个Filter，当有一个过滤条件条件满足时，就通过过滤器。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public class MessageFilterGroup implements IMessageFilter {

    private List<IMessageFilter> mFilterGroup = new ArrayList<>();

    @Override
    public boolean doFilter(String in) {
        for (IMessageFilter filter : mFilterGroup) {
            boolean p = filter.doFilter(in);
            if (p) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加当前过滤器组的过滤器。
     *
     * @param filter 过滤器
     */
    public void addRule(IMessageFilter filter) {
        mFilterGroup.add(filter);
    }

    /**
     * 添加当前过滤器组的过滤器。
     *
     * @param filters 过滤器
     */
    public void addRules(List<IMessageFilter> filters) {
        mFilterGroup.addAll(filters);
    }

    /**
     * 获取当前过滤器组的过滤器集合。
     *
     * @return List 过滤器集合
     */
    public List<IMessageFilter> getRules() {
        return mFilterGroup;
    }
}
