package net.houzuo.android.autonotes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import data.DayDA;
import data.NoteDA;
import dom.Day;
import dom.Note;

public final class DaysActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.days);
		this.dayDA = new DayDA(this);
		this.dayDA.open();
		this.daysListView = (ListView) this.findViewById(R.id.daysListView);
		this.daysListView.setEmptyView(this.findViewById(R.id.noDayTextView));
		this.daysListView.setAdapter(new DaysAdapter(this, this.dayDA.all()));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.dayDA.close();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.dayDA.close();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.dayDA.open();
	}
	
	private final class DaysAdapter extends ArrayAdapter<Day> {
		DaysAdapter(final Context context, final List<Day> days) {
			super(context, R.layout.day, days);
			this.days = days;
		}
		
		@Override
		public View getView(final int position, final View convertView,
						final ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				final LayoutInflater li = (LayoutInflater) DaysActivity.this
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.day, null);
			}
			final Day day = this.days.get(position);
			final TextView dateTextView = (TextView) v
							.findViewById(R.id.dateTextView);
			dateTextView.setText(day.getDate());
			((Button) v.findViewById(R.id.deleteButton))
							.setOnClickListener(new OnClickListener() {
								@SuppressWarnings("synthetic-access")
								@Override
								public final void onClick(final View view) {
									final AlertDialog ad = new AlertDialog.Builder(
													DaysActivity.this).setMessage(
													DaysActivity.this.getString(R.string.sure_to_delete)
																	+ " " + day.getDate() + "?").create();
									ad.setButton("Delete", new DialogInterface.OnClickListener() {
										@Override
										public final void onClick(final DialogInterface dialog,
														final int which) {
											DaysActivity.this.dayDA.delete(day);
											DaysAdapter.this.remove(day);
											DaysAdapter.this.notifyDataSetChanged();
										}
									});
									ad.setButton2("Back", new DialogInterface.OnClickListener() {
										@Override
										public final void onClick(final DialogInterface dialog,
														final int which) {
											dialog.dismiss();
										}
									});
									ad.show();
								}
							});
			((Button) v.findViewById(R.id.exportButton))
							.setOnClickListener(new OnClickListener() {
								@Override
								public final void onClick(final View view) {
									try {
										final File root = Environment.getExternalStorageDirectory();
										final File appDir = new File(root + "/AutoNotes");
										if (!appDir.exists()) {
											appDir.mkdir();
										}
										final File dayText = new File(appDir + "/" + day.getDate()
														+ ".txt");
										if (dayText.exists()) {
											dayText.delete();
										}
										dayText.createNewFile();
										final BufferedWriter br = new BufferedWriter(
														new FileWriter(dayText));
										final NoteDA noteDA = new NoteDA(DaysActivity.this);
										noteDA.open();
										for (final Note n : noteDA.allOf(day.getDayID())) {
											br.write(n.getTime() + ": " + n.getContent() + "\r\n\r\n");
										}
										noteDA.close();
										br.close();
										Toast.makeText(
														DaysActivity.this,
														DaysActivity.this.getString(R.string.export)
																		+ "/AutoNotes/" + day.getDate() + ".txt",
														Toast.LENGTH_LONG).show();
									} catch (final IOException e) {
										Toast.makeText(DaysActivity.this,
														DaysActivity.this.getString(R.string.sd_error),
														Toast.LENGTH_LONG).show();
										return;
									}
								}
							});
			dateTextView.setOnClickListener(new OnClickListener() {
				@Override
				public final void onClick(final View view) {
					DaysActivity.this.startActivity(new Intent(DaysActivity.this,
									NotesActivity.class).putExtra("dayID", day.getDayID())
									.putExtra("date", day.getDate()));
				}
			});
			return v;
		}
		
		private final List<Day> days;
	}
	
	private DayDA dayDA;
	private ListView daysListView;
}
