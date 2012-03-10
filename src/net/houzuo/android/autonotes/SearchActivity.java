package net.houzuo.android.autonotes;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import data.NoteDA;
import dom.Note;

public final class SearchActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search);
		this.noteDA = new NoteDA(this);
		this.noteDA.open();
		this.resultListView = (ListView) this.findViewById(R.id.resultListView);
		this.resultListView.setEmptyView(this.findViewById(R.id.noResultTextView));
		this.keywordEditText = (EditText) this.findViewById(R.id.keywordEditText);
		((Button) this.findViewById(R.id.searchButton))
						.setOnClickListener(new OnClickListener() {
							@SuppressWarnings("synthetic-access")
							@Override
							public final void onClick(final View v) {
								String condition = SearchActivity.this.keywordEditText
												.getText().toString().trim();
								if (condition.startsWith("/") && condition.endsWith("/")) {
									try {
										Pattern.compile(condition.substring(1,
														condition.length() - 1));
										Toast.makeText(
														SearchActivity.this,
														SearchActivity.this
																		.getString(R.string.regex_compiled),
														Toast.LENGTH_SHORT).show();
									} catch (final PatternSyntaxException e) {
										Toast.makeText(
														SearchActivity.this,
														SearchActivity.this
																		.getString(R.string.regex_failure),
														Toast.LENGTH_SHORT).show();
										return;
									}
								} else {
									condition = condition.toLowerCase();
								}
								SearchActivity.this.resultListView
												.setAdapter(new ResultsAdapter(SearchActivity.this,
																SearchActivity.this.noteDA.search(condition)));
								Toast.makeText(
												SearchActivity.this,
												SearchActivity.this.getString(R.string.search_complete),
												Toast.LENGTH_SHORT).show();
							}
						});
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
	
	private final class ResultsAdapter extends ArrayAdapter<Note> {
		ResultsAdapter(final Context context, final List<Note> notes) {
			super(context, R.layout.one_result, notes);
			this.notes = notes;
		}
		
		@Override
		public View getView(final int position, final View convertView,
						final ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = ((LayoutInflater) SearchActivity.this
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
								R.layout.one_result, null);
			}
			final Note n = this.notes.get(position);
			final TextView dateTimeTextView = (TextView) v
							.findViewById(R.id.searchDateTextView);
			dateTimeTextView.setText(n.getDay().getDate() + "\n" + n.getTime());
			final TextView contentTextView = (TextView) v
							.findViewById(R.id.longBirefTextView);
			contentTextView.setText(n.getLongBrief());
			final OnClickListener clickListener = new OnClickListener() {
				@Override
				public final void onClick(final View view) {
					SearchActivity.this.startActivity(new Intent(SearchActivity.this,
									NoteShareActivity.class).putExtra("content", n.getContent()));
				}
			};
			contentTextView.setOnClickListener(clickListener);
			dateTimeTextView.setOnClickListener(clickListener);
			return v;
		}
		
		private final List<Note> notes;
	}
	
	private EditText keywordEditText;
	private NoteDA noteDA;
	private ListView resultListView;
}
