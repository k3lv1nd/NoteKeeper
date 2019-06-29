package kelvin.notekeeper.com;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import kelvin.notekeeper.com.NoteKeeperDatabaseContract.NoteInfoEntry;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "kelvin.notekeeper.com.notekeeper2.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "kelvin.notekeeper.com.notekeeper2.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "kelvin.notekeeper.com.notekeeper2.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "kelvin.notekeeper.com.notekeeper2.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private int position;
    private NoteKeeperOpenHelper mdbOpenHelper;
    private Cursor noteCursor;
    private int courseIdPos;
    private int noteTitlePos;
    private int noteTextPos;

    @Override
    protected void onDestroy() {
        mdbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mdbOpenHelper = new NoteKeeperOpenHelper(this);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);
        
        readDispalyValues();
        if(savedInstanceState== null){
            saveOriginalNoteValues();
        }else{
            restoreOriginalNoteValues(savedInstanceState);
        }


        textNoteTitle = (EditText) findViewById(R.id.text_note_title);
        textNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!mIsNewNote)
       loadNoteData();
        Log.d(TAG, "onCreate");

    }

    private void loadNoteData() {
        SQLiteDatabase db = mdbOpenHelper.getReadableDatabase();

        String courseId = "android_intents";
        String titleStart = "dynamic";

        String selection = NoteInfoEntry.COLUMN_COURSE_ID+ " = ? AND " +
                NoteInfoEntry.COLUMN_NOTE_TITLE +" LIKE ? ";

        String [] selectionArgs = {courseId, titleStart + " % "};

        String  [] noteColumns = {
          NoteInfoEntry.COLUMN_COURSE_ID,
          NoteInfoEntry.COLUMN_NOTE_TITLE,
          NoteInfoEntry.COLUMN_NOTE_TEXT
        };

        noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                selection, selectionArgs, null, null, null);
        courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        while(noteCursor.moveToNext()){
            displayNote();
        }



    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {

        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote)
            return;
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            Log.i(TAG, "Cancelling at position: "+ mNotePosition);
            if(mIsNewNote){

                DataManager.getInstance().removeNote(mNotePosition);
            }else{
                storePreviousNoteValues();
            }

        }else{
            saveNote();
        }

        Log.d(TAG, "onPause");

    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setText(mOriginalNoteText);
        mNote.setTitle(mOriginalNoteTitle);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
        outState.putString(ORIGINAL_NOTE_TITLE,mOriginalNoteTitle);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        mNote.setTitle(textNoteTitle.getText().toString());
        mNote.setText(textNoteText.getText().toString());

    }

    private void displayNote() {
        String courseId = noteCursor.getString(courseIdPos);
        String noteTitle = noteCursor.getString(noteTitlePos);
        String noteText = noteCursor.getString(noteTextPos);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        CourseInfo course = DataManager.getInstance().getCourse(courseId);
        int courseIndex = courses.indexOf(course);
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(noteTitle);
        textNoteText.setText(noteText);


        
    }

    private void readDispalyValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if(mIsNewNote){
           createNewNote();

        }

        Log.i(TAG, "mNotePosition: " + mNotePosition);
            mNote = DataManager.getInstance().getNotes().get(mNotePosition);


    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
      //  mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }
        else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();
        }else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size()-1;
        item.setEnabled(mNotePosition<lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();

    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String text = "Checkout what i have been learning at pluralsight\""+
                course.getTitle() + "\"\n" + textNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);

    }


}
