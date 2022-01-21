package cn.desert.newpos.payui.transrecord;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ListAdapter<T> extends BaseAdapter implements RecyclerListener {

    protected final ScrollListenersAdapter defaultListnerAdapter = new ScrollListenersAdapter();
    protected List<T> mList;
    protected Activity mContext;
    protected ListView mListView;

    protected ListAdapter(Activity context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setList(T[] list) {
        ArrayList<T> arrayList = new ArrayList<>(list.length);
        arrayList.addAll(Arrays.asList(list));
        setList(arrayList);
    }

    public void addAll(List<T> list) {
        this.mList.addAll(list);
    }

    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }

    public Object getItemAtPosition(int position) {
        return mList.get(position);
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        //可继承此方法，在List的某一项被回收的时候使用。
    }

    public ScrollListenersAdapter getListnersAdapter() {
        return defaultListnerAdapter;
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    /**
     * 适配器模式，使可设置监听器数量由1个转为多个。
     *
     * @author LuDaiqian
     */
    public class ScrollListenersAdapter implements OnScrollListener {
        protected final ArrayList<OnScrollListener> mListeners = new ArrayList<>();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            for (OnScrollListener l : mListeners) {
                l.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            for (OnScrollListener l : mListeners) {
                l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

        public void addOnScrollListener(OnScrollListener l) {
            mListeners.add(l);
        }
    }
}
