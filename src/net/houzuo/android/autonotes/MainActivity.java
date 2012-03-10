package net.houzuo.android.autonotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public final class MainActivity extends Activity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.autosaveToggleButton = (ToggleButton) this
						.findViewById(R.id.autosaveToggleButton);
		this.autosaveToggleButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public final void onClick(final View v) {
				if (AutoNotesService.instance == null) {
					MainActivity.this.startService(new Intent(MainActivity.this,
									AutoNotesService.class));
					MainActivity.this.autosaveToggleButton.setChecked(true);
					Toast.makeText(
									MainActivity.this,
									MainActivity.this
													.getString(R.string.manually_enable_accessibility),
									Toast.LENGTH_LONG).show();
					MainActivity.this.startActivity(new Intent(
									android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				} else {
					AutoNotesService.instance.stopSelf();
					MainActivity.this.autosaveToggleButton.setChecked(false);
					if (AutoNotesService.instance != null) {
						Toast.makeText(
										MainActivity.this,
										MainActivity.this
														.getString(R.string.manually_disable_accessibility),
										Toast.LENGTH_LONG).show();
						MainActivity.this.startActivity(new Intent(
										android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
										.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					}
				}
			}
		});
		this.clipboardToggleButton = (ToggleButton) this
						.findViewById(R.id.clipboardToggleButton);
		this.clipboardToggleButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public final void onClick(final View v) {
				final Editor perfEditor = MainActivity.this.sp.edit();
				perfEditor.putBoolean("clipboardBypass",
								!MainActivity.this.clipboardToggleButton.isChecked());
				AutoNotesService.clipboardBypass = !MainActivity.this.clipboardToggleButton
								.isChecked();
				perfEditor.commit();
			}
		});
		this.messageToggleButton = (ToggleButton) this
						.findViewById(R.id.messageToggleButton);
		this.messageToggleButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public final void onClick(final View v) {
				final Editor perfEditor = MainActivity.this.sp.edit();
				perfEditor.putBoolean("messageBypass",
								!MainActivity.this.messageToggleButton.isChecked());
				AutoNotesService.messageBypass = !MainActivity.this.messageToggleButton
								.isChecked();
				perfEditor.commit();
			}
		});
		((Button) this.findViewById(R.id.setKeywordsButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								MainActivity.this.startActivity(new Intent(MainActivity.this,
												KeywordsActivity.class));
							}
						});
		((Button) this.findViewById(R.id.notesButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								MainActivity.this.startActivity(new Intent(MainActivity.this,
												DaysActivity.class));
							}
						});
		this.minWordsEditText = (EditText) this.findViewById(R.id.minWordsEditText);
		this.minWordsEditText.addTextChangedListener(new TextWatcher() {
			@SuppressWarnings("boxing")
			@Override
			public final void afterTextChanged(final Editable arg0) {
				try {
					@SuppressWarnings("synthetic-access")
					final Editor perfEditor = MainActivity.this.sp.edit();
					perfEditor.putInt("minNumberWords", Integer.valueOf(arg0.toString()));
					AutoNotesService.minNumberWords = Integer.valueOf(arg0.toString());
					perfEditor.commit();
				} catch (final NumberFormatException e) {
					AutoNotesService.minNumberWords = 2;
				}
			}
			
			@Override
			public final void beforeTextChanged(final CharSequence s,
							final int start, final int count, final int after) {
				return;
			}
			
			@Override
			public final void onTextChanged(final CharSequence s, final int start,
							final int before, final int count) {
				return;
			}
		});
		((Button) this.findViewById(R.id.clipboardHelpButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								final AlertDialog ad = new AlertDialog.Builder(
												MainActivity.this).setMessage(
												R.string.filter_clipboard_help).create();
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
		((Button) this.findViewById(R.id.messageHelpButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								final AlertDialog ad = new AlertDialog.Builder(
												MainActivity.this).setMessage(
												R.string.filter_message_help).create();
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
		((Button) this.findViewById(R.id.minWordsHelpButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								final AlertDialog ad = new AlertDialog.Builder(
												MainActivity.this).setMessage(R.string.min_words_help)
												.create();
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
		((Button) this.findViewById(R.id.contactButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								final AlertDialog ad = new AlertDialog.Builder(
												MainActivity.this).setMessage(R.string.min_words_help)
												.create();
								ad.setMessage(MainActivity.this
												.getString(R.string.contact_author));
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
		((Button) this.findViewById(R.id.searchButton))
						.setOnClickListener(new OnClickListener() {
							@Override
							public final void onClick(final View v) {
								MainActivity.this.startActivity(new Intent(MainActivity.this,
												SearchActivity.class));
							}
						});
		this.sp = this.getSharedPreferences("AutoNotes", 0);
		this.load();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.load();
	}
	
	private void load() {
		final boolean messageBypass = this.sp.getBoolean("messageBypass", true);
		final boolean clipboardBypass = this.sp.getBoolean("clipboardBypass", true);
		final int minWords = this.sp.getInt("minNumberWords", 2);
		AutoNotesService.messageBypass = messageBypass;
		AutoNotesService.clipboardBypass = clipboardBypass;
		AutoNotesService.minNumberWords = minWords;
		this.autosaveToggleButton.setChecked(AutoNotesService.instance != null);
		this.messageToggleButton.setChecked(!messageBypass);
		this.clipboardToggleButton.setChecked(!clipboardBypass);
		this.minWordsEditText.setText(String.valueOf(minWords));
	}
	
	private ToggleButton autosaveToggleButton, clipboardToggleButton,
					messageToggleButton;
	private EditText minWordsEditText;
	private SharedPreferences sp;
}