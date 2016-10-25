package edu.uwm.ibidder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Brandon on 10/25/16.
 * stackoverflow.com/questions/24885223/why-doesnt-recyclerview-have-onitemclicklistener-and-how-recyclerview-is-diff
 */

public class ItemClickSupport {
    private final RecyclerView m_recyclerView;
    private OnItemClickListener m_onItemClickListener;
    private OnItemLongClickListener m_onItemLongClickListener;

    private View.OnClickListener m_onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(m_onItemClickListener != null){
                RecyclerView.ViewHolder holder = m_recyclerView.getChildViewHolder(v);
                m_onItemClickListener.onItemClicked(m_recyclerView, holder.getAdapterPosition(), v);
            }
        }
    };

    private View.OnLongClickListener m_onLongClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v){
            if(m_onLongClickListener != null){
                RecyclerView.ViewHolder holder = m_recyclerView.getChildViewHolder(v);
                m_onItemLongClickListener.onItemLongClicked(m_recyclerView, holder.getAdapterPosition(), v);
            }

            return false;
        }
    };

    private RecyclerView.OnChildAttachStateChangeListener m_attachListener = new RecyclerView.OnChildAttachStateChangeListener(){
        @Override
        public void onChildViewAttachedToWindow(View view) {
            // every time a child view is attached add click listeners to it
            if(m_onItemClickListener != null){
                view.setOnClickListener(m_onClickListener);
            }
            if(m_onItemLongClickListener != null){
                view.setOnLongClickListener(m_onLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) { }
    };

    /* Constructor */
    private ItemClickSupport(RecyclerView rv){
        m_recyclerView = rv;
        m_recyclerView.setTag(R.id.item_click_support, this);
        m_recyclerView.addOnChildAttachStateChangeListener(m_attachListener);
    }

    public static ItemClickSupport addTo(RecyclerView rv){
        // check if there is an ItemClickSupport already attached
        ItemClickSupport ics = (ItemClickSupport) rv.getTag(R.id.item_click_support);
        if(ics == null){
            // attach
            ics = new ItemClickSupport(rv);
        }

        return ics;
    }


    private void detach(RecyclerView rv){
        rv.removeOnChildAttachStateChangeListener(m_attachListener);
        rv.setTag(R.id.item_click_support, null);
    }

    public static ItemClickSupport removeFrom(RecyclerView rv){
        ItemClickSupport ics = (ItemClickSupport) rv.getTag(R.id.item_click_support);
        if(ics != null){
            ics.detach(rv);
        }

        return ics;
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener){
        m_onItemClickListener = listener;
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener){
        m_onItemLongClickListener = listener;
        return this;
    }

    public interface OnItemClickListener{
        void onItemClicked(RecyclerView rv, int position, View v);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClicked(RecyclerView rv, int postion, View v);
    }


}
