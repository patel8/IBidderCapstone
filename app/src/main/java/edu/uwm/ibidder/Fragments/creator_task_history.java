package edu.uwm.ibidder.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import edu.uwm.ibidder.DividerItemDecoration;
import edu.uwm.ibidder.ItemClickSupport;
import edu.uwm.ibidder.R;
import edu.uwm.ibidder.Activities.TaskActivityII;
import edu.uwm.ibidder.dbaccess.TaskAccessor;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class creator_task_history extends Fragment {

    private final String FRAGMENT_NAME = "creator_task_history";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRecyclerAdapter<TaskModel, viewHolder> adapter;
    private Paint p = new Paint();

    public creator_task_history()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.ascreator_history);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_creator_task_history, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_creator_task_history);
        recyclerView = (RecyclerView) v.findViewById(R.id.creator_task_history_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity()));
        TaskAccessor ta = new TaskAccessor();
        Query q = ta.getTasksByOwnerIdQuery(FirebaseAuth.getInstance().getCurrentUser().getUid()
                , TaskModel.TaskStatusType.FINISHED.toString());
        adapter = new FirebaseRecyclerAdapter<TaskModel, viewHolder>(
                TaskModel.class,
                R.layout.bidder_current_task_list_template,
                viewHolder.class,
                q
        ) {
            @Override
            protected void populateViewHolder(viewHolder viewHolder, TaskModel taskModel, int i) {
                viewHolder.title.setText(taskModel.getTitle());
                viewHolder.DateTime.setText(taskModel.getExpirationTime() + "");
                viewHolder.Price.setText(taskModel.getMaxPrice() + "");
                viewHolder.distance.setVisibility(View.GONE);

                viewHolder.taskItNow.setVisibility(taskModel.getIsTaskItNow()?View.VISIBLE:View.GONE);
            }
        };

        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                TaskModel tm = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskActivityII.class);
                intent.putExtra("task_id", tm.getTaskId());
                intent.putExtra("task_status", tm.getStatus().toString());
                intent.putExtra("caller", FRAGMENT_NAME);
                startActivity(intent);
            }
        });
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                TaskModel tm = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskActivityII.class);
                intent.putExtra("task_id", tm.getTaskId());
                intent.putExtra("task_status", tm.getStatus().toString());
                intent.putExtra("caller", FRAGMENT_NAME);
                startActivity(intent);
            }
        });
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                adapter.getRef(viewHolder.getAdapterPosition()).removeValue();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.deletetask);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.deletetask);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        });
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView);
        return v;
    }


}
