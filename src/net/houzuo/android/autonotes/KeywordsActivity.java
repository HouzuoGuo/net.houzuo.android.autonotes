package net.houzuo.android.autonotes;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.ToggleButton;
import data.KeywordDA;
import dom.Keyword;

public class KeywordsActivity extends Activity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.keywords);
		((Button) this.findViewById(R.id.newKeywordButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								final AlertDialog.Builder inputDialog = new AlertDialog.Builder(
												KeywordsActivity.this);
								inputDialog.setTitle(KeywordsActivity.this
												.getString(R.string.new_keyword));
								final EditText keywordEditText = new EditText(
												KeywordsActivity.this);
								inputDialog.setView(keywordEditText);
								inputDialog.setMessage(KeywordsActivity.this
												.getString(R.string.keywords_examples));
								inputDialog.setPositiveButton("OK",
												new DialogInterface.OnClickListener() {
													@SuppressWarnings("synthetic-access")
													@Override
													public final void onClick(
																	final DialogInterface dialog, final int which) {
														String kw = keywordEditText.getText().toString();
														kw = kw.trim();
														if (!kw.equals("")) {
															if (kw.startsWith("/") && kw.endsWith("/")) {
																try {
																	Pattern.compile(kw.substring(1,
																					kw.length() - 1));
																	Toast.makeText(
																					KeywordsActivity.this,
																					KeywordsActivity.this
																									.getString(R.string.regex_compiled),
																					Toast.LENGTH_SHORT).show();
																} catch (final PatternSyntaxException e) {
																	Toast.makeText(
																					KeywordsActivity.this,
																					KeywordsActivity.this
																									.getString(R.string.regex_failure),
																					Toast.LENGTH_LONG).show();
																	return;
																}
															} else {
																kw = kw.toLowerCase();
															}
															if (AutoNotesService.keywords.contains(kw)) {
																Toast.makeText(
																				KeywordsActivity.this,
																				KeywordsActivity.this
																								.getString(R.string.keyword_already_exists),
																				Toast.LENGTH_SHORT).show();
															} else {
																KeywordsActivity.this.keywordDA.create(kw);
																@SuppressWarnings("unchecked")
																final ArrayAdapter<Keyword> adapter = (ArrayAdapter<Keyword>) KeywordsActivity.this.keywordsListView
																				.getAdapter();
																adapter.add(new Keyword(kw));
																adapter.notifyDataSetChanged();
																AutoNotesService
																				.updateKeywords(KeywordsActivity.this.keywordDA
																								.all());
																Toast.makeText(
																				KeywordsActivity.this,
																				KeywordsActivity.this
																								.getString(R.string.keyword_created),
																				Toast.LENGTH_SHORT).show();
															}
														}
													}
												});
								inputDialog.setNegativeButton("Cancel",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(final DialogInterface dialog,
																	final int which) {
														dialog.dismiss();
													}
												});
								inputDialog.show();
							}
						});
		((Button) this.findViewById(R.id.whatButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								final AlertDialog ad = new AlertDialog.Builder(
												KeywordsActivity.this).setMessage(
												R.string.what_actually_are_keywords).create();
								ad.setButton("OK", new DialogInterface.OnClickListener() {
									@Override
									public final void onClick(final DialogInterface dialog,
													final int which) {
										dialog.dismiss();
									}
								});
								ad.show();
							}
						});
		this.notifyToggleButton = (ToggleButton) this
						.findViewById(R.id.notifyToggleButton);
		final SharedPreferences sp = KeywordsActivity.this.getSharedPreferences(
						"AutoNotes", 0);
		this.notifyToggleButton.setChecked(sp.getBoolean("notifyMatch", true));
		this.notifyToggleButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public final void onClick(final View v) {
				final Editor prefEditor = sp.edit();
				prefEditor.putBoolean("notifyMatch",
								KeywordsActivity.this.notifyToggleButton.isChecked());
				AutoNotesService.notifyMatch = KeywordsActivity.this.notifyToggleButton
								.isChecked();
				prefEditor.commit();
			}
		});
		this.keywordsListView = (ListView) this.findViewById(R.id.keywordsListView);
		this.keywordsListView.setEmptyView(this.findViewById(R.id.nothingTextView));
		this.keywordDA = new KeywordDA(this);
		this.keywordDA.open();
		this.keywordsListView.setAdapter(new KeywordsAdapter(this, this.keywordDA
						.all()));
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.keywordDA.close();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.keywordDA.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.keywordDA.open();
	}
	
	private final class KeywordsAdapter extends ArrayAdapter<Keyword> {
		KeywordsAdapter(final Context context, final List<Keyword> keywords) {
			super(context, R.layout.keyword, keywords);
			this.keywords = keywords;
		}
		
		@Override
		public View getView(final int position, final View convertView,
						final ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = ((LayoutInflater) KeywordsActivity.this
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
								R.layout.keyword, null);
			}
			final Keyword kw = this.keywords.get(position);
			final TextView keywordTextView = (TextView) v
							.findViewById(R.id.keywordTextView);
			final Button removeKeywordButton = (Button) v
							.findViewById(R.id.removeKeywordButton);
			keywordTextView.setText(kw.getWord());
			removeKeywordButton.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("synthetic-access")
				@Override
				public final void onClick(final View view) {
					KeywordsActivity.this.keywordDA.delete(kw);
					AutoNotesService.updateKeywords(KeywordsActivity.this.keywordDA.all());
					KeywordsAdapter.this.remove(kw);
					KeywordsAdapter.this.notifyDataSetChanged();
				}
			});
			return v;
		}
		
		private final List<Keyword> keywords;
	}
	
	private KeywordDA keywordDA;
	private ListView keywordsListView;
	private ToggleButton notifyToggleButton;
}
