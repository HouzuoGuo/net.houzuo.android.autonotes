package net.houzuo.android.autonotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public final class NoteShareActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.note_share);
		this.contentEditText = (EditText) this.findViewById(R.id.contentEditText);
		this.contentEditText.setText(this.getIntent().getExtras()
						.getString("content"));
		((Button) this.findViewById(R.id.shareButton))
						.setOnClickListener(new OnClickListener() {
							@SuppressWarnings("synthetic-access")
							@Override
							public final void onClick(final View v) {
								NoteShareActivity.this.startActivity(new Intent(
												android.content.Intent.ACTION_SEND)
												.setType("text/plain")
												.putExtra(android.content.Intent.EXTRA_SUBJECT,
																"AutoNote")
												.putExtra(
																android.content.Intent.EXTRA_TEXT,
																NoteShareActivity.this.contentEditText
																				.getText().toString()));
							}
						});
		((Button) this.findViewById(R.id.copyButton))
						.setOnClickListener(new OnClickListener() {
							@SuppressWarnings("synthetic-access")
							@Override
							public final void onClick(final View v) {
								((ClipboardManager) NoteShareActivity.this
												.getSystemService(Context.CLIPBOARD_SERVICE))
												.setText(NoteShareActivity.this.contentEditText
																.getText().toString());
								Toast.makeText(
												NoteShareActivity.this,
												NoteShareActivity.this
																.getString(R.string.copied_to_clipboard),
												Toast.LENGTH_SHORT).show();
							}
						});
	}
	
	private EditText contentEditText;
}
