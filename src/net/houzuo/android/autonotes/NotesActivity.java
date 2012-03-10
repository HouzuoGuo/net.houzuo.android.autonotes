package net.houzuo.android.autonotes;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import data.NoteDA;
import dom.Note;

public final class NotesActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.notes);
		this.noteDA = new NoteDA(this);
		this.noteDA.open();
		this.notesListView = (ListView) this.findViewById(R.id.notesListView);
		this.notesListView.setEmptyView(this.findViewById(R.id.noNoteTextView));
		final Bundle extras = this.getIntent().getExtras();
		this.dayID = extras.getLong("dayID");
		this.notesListView.setAdapter(new NotesAdapter(this, this.noteDA
						.allOf(this.dayID)));
		((TextView) this.findViewById(R.id.dateTextView)).setText(extras
						.getString("date"));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.noteDA.close();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.noteDA.close();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.noteDA.open();
	}
	
	private final class NotesAdapter extends ArrayAdapter<Note> {
		NotesAdapter(final Context context, final List<Note> notes) {
			super(context, R.layout.note, notes);
			this.notes = notes;
		}
		
		@Override
		public View getView(final int position, final View convertView,
						final ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				final LayoutInflater li = (LayoutInflater) NotesActivity.this
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.note, null);
			}
			final Note note = this.notes.get(position);
			final Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
			final TextView briefTextView = (TextView) v
							.findViewById(R.id.briefTextView), timeTextView = (TextView) v
							.findViewById(R.id.timeTextView);
			final OnClickListener clickListener = new OnClickListener() {
				@Override
				public final void onClick(final View view) {
					NotesActivity.this.startActivity(new Intent(NotesActivity.this,
									NoteShareActivity.class).putExtra("content",
									note.getContent()));
				}
			};
			briefTextView.setText(note.getBrief());
			briefTextView.setOnClickListener(clickListener);
			timeTextView.setText(note.getTime());
			timeTextView.setOnClickListener(clickListener);
			deleteButton.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("synthetic-access")
				@Override
				public final void onClick(final View view) {
					NotesActivity.this.noteDA.delete(note);
					NotesAdapter.this.remove(note);
					NotesAdapter.this.notifyDataSetChanged();
				}
			});
			return v;
		}
		
		private final List<Note> notes;
	}
	
	private long dayID;
	private NoteDA noteDA;
	private ListView notesListView;
}
