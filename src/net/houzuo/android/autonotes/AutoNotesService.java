package net.houzuo.android.autonotes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcelable;
import android.text.ClipboardManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import data.DayDA;
import data.KeywordDA;
import data.NoteDA;
import dom.Day;
import dom.Keyword;

public final class AutoNotesService extends AccessibilityService {
	
	@Override
	public void onAccessibilityEvent(final AccessibilityEvent event) {
		switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_VIEW_CLICKED:
		case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
		case AccessibilityEvent.TYPE_VIEW_FOCUSED:
			if (this.lastTyped != null) {
				this.save(this.lastTyped, AutoNotesService.messageBypass);
			}
			break;
		case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
			if (event.getText().size() > 0) {
				this.lastTyped = event.getText().get(0).toString();
			}
		}
		if (event.getEventType() != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
			final CharSequence description = event.getContentDescription();
			this.save(AutoNotesService.join(event.getText(), "\n"), false);
			if (description != null) {
				this.save(description.toString(), false);
			}
			final Parcelable data = event.getParcelableData();
			if (data != null) {
				this.save(data.toString(), false);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		this.dayDA.close();
		this.noteDA.close();
		this.nm.cancelAll();
		AutoNotesService.instance = null;
	}
	
	@Override
	public void onInterrupt() {
		return;
	}
	
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		this.noteDA = new NoteDA(this);
		this.noteDA.open();
		this.dayDA = new DayDA(this);
		this.dayDA.open();
		final KeywordDA keywordDA = new KeywordDA(this);
		keywordDA.open();
		AutoNotesService.updateKeywords(keywordDA.all());
		keywordDA.close();
		AutoNotesService.instance = this;
		
		final SharedPreferences sp = this.getSharedPreferences("AutoNotes", 0);
		AutoNotesService.messageBypass = sp.getBoolean("messageBypass", true);
		AutoNotesService.clipboardBypass = sp.getBoolean("clipboardBypass", true);
		AutoNotesService.minNumberWords = sp.getInt("minNumberWords", 2);
		AutoNotesService.notifyMatch = sp.getBoolean("notifyMatch", true);
		
		this.cm = (ClipboardManager) this
						.getSystemService(Context.CLIPBOARD_SERVICE);
		this.nm = (NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(R.drawable.ic_launcher,
						this.getString(R.string.app_name), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(this.getApplicationContext(), this
						.getString(R.string.app_name), this
						.getString(R.string.notification_text), PendingIntent.getActivity(
						this, 0, new Intent(this, MainActivity.class), 0));
		this.nm.notify(AutoNotesService.NOTIFICATION_ID, notification);
		this.clipboardHandler.post(this.clipboardMonitor);
		this.configAccessibility();
	}
	
	private void configAccessibility() {
		final AccessibilityServiceInfo config = new AccessibilityServiceInfo();
		config.flags = AccessibilityServiceInfo.DEFAULT;
		config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		config.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		this.setServiceInfo(config);
	}
	
	private synchronized void save(final String content, final boolean bypass) {
		if (content == null) {
			return;
		}
		if (content.length() < 3) {
			return;
		}
		String toSave = content;
		if (toSave.charAt(0) == '[') {
			if (toSave.length() < 3) {
				return;
			}
			toSave = content.substring(1, content.length() - 1);
		}
		if (!bypass & toSave.split(" ").length < AutoNotesService.minNumberWords) {
			return;
		}
		toSave = toSave.trim();
		if (toSave.length() == 0) {
			return;
		}
		toSave += " ";
		if (this.recent.contains(toSave)) {
			return;
		}
		if (!bypass) {
			boolean keywordFound = false;
			for (final String kw : AutoNotesService.keywords) {
				if (toSave.toLowerCase().contains(kw) || kw.startsWith("/")
								&& kw.endsWith("/")
								&& toSave.matches(kw.substring(1, kw.length() - 1))) {
					keywordFound = true;
					if (AutoNotesService.notifyMatch) {
						Toast.makeText(
										this,
										"\""
														+ toSave.substring(0,
																		toSave.length() < 25 ? toSave.length() : 25)
														+ "\".. matched \"" + kw + "\"", Toast.LENGTH_SHORT)
										.show();
					}
					break;
				}
			}
			if (!keywordFound) {
				return;
			}
		}
		final String todayDate = new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date());
		this.today = this.dayDA.findDate(todayDate);
		if (this.today == null || !this.today.getDate().equals(todayDate)) {
			this.today = this.dayDA.create(todayDate);
		}
		// Create note
		this.noteDA.create(this.today, toSave,
						new SimpleDateFormat("HH:mm:ss").format(new Date()));
		this.recent.add(toSave);
		if (this.recent.size() > AutoNotesService.RECENT_ENTRIES - 1) {
			this.recent.remove(0);
		}
	}
	
	public static String join(final Collection<?> s, final String delimiter) {
		final StringBuilder builder = new StringBuilder();
		final Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}
	
	public static void updateKeywords(final List<Keyword> kws) {
		AutoNotesService.keywords.clear();
		for (final Keyword kw : kws) {
			AutoNotesService.keywords.add(kw.getWord());
		}
	}
	
	public final static long CLIPBOARD_MONITOR_INTERVAL = 1000;
	public static boolean clipboardBypass = true, messageBypass = true;
	public static AutoNotesService instance = null;
	public static List<String> keywords = new ArrayList<String>();
	public static int minNumberWords = 2;
	public final static int NOTIFICATION_ID = 1;
	public static boolean notifyMatch = true;
	public final static int RECENT_ENTRIES = 20;
	private final Handler clipboardHandler = new Handler();
	private final Runnable clipboardMonitor = new Runnable() {
		@SuppressWarnings("synthetic-access")
		@Override
		public final void run() {
			if (AutoNotesService.this.cm.hasText()) {
				final CharSequence cs = AutoNotesService.this.cm.getText();
				if (cs != null) {
					final String text = cs.toString();
					if (!text.equals(AutoNotesService.this.lastClipboard)) {
						AutoNotesService.this.save(text.toString(),
										AutoNotesService.clipboardBypass);
						AutoNotesService.this.lastClipboard = text;
					}
				}
			}
			AutoNotesService.this.clipboardHandler.postDelayed(
							AutoNotesService.this.clipboardMonitor,
							AutoNotesService.CLIPBOARD_MONITOR_INTERVAL);
		}
	};
	private ClipboardManager cm;
	private DayDA dayDA;
	private String lastClipboard = "";
	private String lastTyped = "";
	private NotificationManager nm;
	private NoteDA noteDA;
	private final List<String> recent = new ArrayList<String>(
					AutoNotesService.RECENT_ENTRIES);
	private Day today;
}
