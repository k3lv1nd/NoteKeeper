package kelvin.notekeeper.com;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.viewHolder> {

    private final Context mContext;
    private final LayoutInflater mlayoutInflator;
    private final List<CourseInfo> mCourses;

    public CourseRecyclerAdapter(Context mContext, List<CourseInfo> mCourses) {
        this.mContext = mContext;
        mlayoutInflator = LayoutInflater.from(mContext);
        this.mCourses = mCourses;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mlayoutInflator.inflate(R.layout.item_course_list, viewGroup, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {

        CourseInfo course = mCourses.get(i);
        viewHolder.mtextCourse.setText(course.getTitle());
        viewHolder.mCurrentPosition = i;


    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

       public final TextView mtextCourse;
       public int mCurrentPosition;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mtextCourse = (TextView) itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,mCourses.get(mCurrentPosition).getTitle(),Snackbar.LENGTH_LONG).show();


                }
            });
        }
    }

}
