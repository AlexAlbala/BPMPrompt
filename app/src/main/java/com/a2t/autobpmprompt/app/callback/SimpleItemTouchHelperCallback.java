package com.a2t.autobpmprompt.app.callback;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.DragEvent;
import android.view.View;

import com.a2t.autobpmprompt.app.lib.LogUtils;
import com.a2t.autobpmprompt.app.helpers.ItemTouchHelperAdapter;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean dragEnabled;
    private int dragFrom = -1;
    private int dragTo = -1;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
        dragEnabled = true;
    }

    public void setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
    }


    @Override
    public boolean isLongPressDragEnabled() {
        return dragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.ACTION_STATE_DRAG | ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.END | ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();


        if (dragFrom == -1) {
            dragFrom = fromPosition;
        }
        dragTo = toPosition;

        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        mAdapter.onItemDropped(dragFrom, dragTo);
        dragFrom = dragTo = -1;
    }
}