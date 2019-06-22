package kelvin.notekeeper.com;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter  extends RecyclerView.Adapter<NoteRecyclerAdapter.viewHolder> {

    private final Context mContext;
    private final LayoutInflater mlayoutInflator;
    private final List<NoteInfo> mNotes;

    public NoteRecyclerAdapter(Context mContext, List<NoteInfo> mNotes) {
        this.mContext = mContext;
        mlayoutInflator = LayoutInflater.from(mContext);
        this.mNotes = mNotes;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mlayoutInflator.inflate(R.layout.item_note_list, viewGroup, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {

        NoteInfo note = mNotes.get(i);
        viewHolder.mtextCourse.setText(note.getCourse().getTitle());
        viewHolder.mtextTitle.setText(note.getTitle());
        viewHolder.mCurrentPosition = i;


    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

       public final TextView mtextCourse;
       public final TextView mtextTitle;
       public int mCurrentPosition;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mtextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mtextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(MainActivity.NOTE_POSITION, mCurrentPosition);
                    mContext.startActivity(intent);


                }
            });
        }
    }

}
