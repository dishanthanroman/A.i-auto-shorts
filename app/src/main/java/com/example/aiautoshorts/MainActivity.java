package com.example.aiautoshorts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private static final int PICK_VIDEO_REQUEST = 1001;

    private TextView selectedFileText;
    private Uri selectedVideoUri;

    private EditText youtubeUrl, driveUrl;
    private Spinner targetPlatform, duration, captionStyle, aspectRatio;
    private CheckBox captions, vertical, bestMoments, autoFraming;
    private TextView status;
    private LinearLayout savedClipsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(15, 23, 42)); // Slate dark background

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(32, 40, 32, 40);
        scroll.addView(page);

        // App Title
        TextView title = new TextView(this);
        title.setText("🎬 AI AUTO SHORTS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        page.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Convert long videos into viral short clips with AI");
        subtitle.setTextColor(Color.rgb(148, 163, 184));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 24);
        page.addView(subtitle);

        // Section 1: Video Input
        page.addView(sectionHeader("1. Select Source Video"));

        // File upload button
        Button uploadBtn = createStyledButton("📁 Upload Local Video File", Color.rgb(37, 99, 235));
        page.addView(uploadBtn);

        selectedFileText = new TextView(this);
        selectedFileText.setText("No file selected");
        selectedFileText.setTextColor(Color.rgb(148, 163, 184));
        selectedFileText.setTextSize(13);
        selectedFileText.setPadding(8, 6, 8, 16);
        page.addView(selectedFileText);

        uploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(Intent.createChooser(intent, "Select Video File"), PICK_VIDEO_REQUEST);
        });

        // URL inputs
        youtubeUrl = input("🔗 YouTube Video URL (e.g., https://youtube.com/watch?v=...)");
        page.addView(youtubeUrl);

        driveUrl = input("🔗 Google Drive / Direct Video Link");
        page.addView(driveUrl);

        // Section 2: Platform & Output Configuration
        page.addView(sectionHeader("2. Target Platform & Clip Settings"));

        TextView pLabel = label("Target Platform");
        page.addView(pLabel);
        targetPlatform = createSpinner(new String[]{"📱 YouTube Shorts", "📸 Instagram Reels", "🎵 TikTok"});
        page.addView(targetPlatform);

        TextView dLabel = label("Clip Duration");
        page.addView(dLabel);
        duration = createSpinner(new String[]{"🤖 Auto AI Detection (Optimal)", "⚡ 15 Seconds", "🔥 30 Seconds", "⏳ 60 Seconds"});
        page.addView(duration);

        TextView arLabel = label("Aspect Ratio / Format");
        page.addView(arLabel);
        aspectRatio = createSpinner(new String[]{"📱 Vertical 9:16 (Shorts/Reels/TikTok)", "🔲 Square 1:1 (Feed)", "📺 Landscape 16:9 (Original)"});
        page.addView(aspectRatio);

        // Section 3: AI Features & Captions
        page.addView(sectionHeader("3. AI Features & Captions Style"));

        TextView csLabel = label("Subtitle / Caption Style");
        page.addView(csLabel);
        captionStyle = createSpinner(new String[]{"✨ Bold Yellow (Viral Style)", "🔥 Bold White with Black Outline", "💎 Neon Cyan Highlight", "📝 Minimal Clean Subtitles"});
        page.addView(captionStyle);

        bestMoments = check("🤖 AI Highlight Detection & Auto-Clipping");
        bestMoments.setChecked(true);
        page.addView(bestMoments);

        captions = check("📝 Auto Subtitles & Stylish Captions");
        captions.setChecked(true);
        page.addView(captions);

        vertical = check("📱 Vertical 9:16 Reframing & Dynamic Crop");
        vertical.setChecked(true);
        page.addView(vertical);

        autoFraming = check("🎯 AI Speaker Tracking & Auto-Framing");
        autoFraming.setChecked(true);
        page.addView(autoFraming);

        // Action Button
        Button create = createStyledButton("🚀 CREATE SHORTS", Color.rgb(225, 29, 72));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(-1, -2);
        btnLp.setMargins(0, 24, 0, 16);
        create.setLayoutParams(btnLp);
        page.addView(create);

        // Status view
        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setTextSize(14);
        status.setPadding(12, 12, 12, 12);
        page.addView(status);

        // Section 4: Saved / Generated Clips
        page.addView(sectionHeader("💾 Saved Clips & History"));

        savedClipsLayout = new LinearLayout(this);
        savedClipsLayout.setOrientation(LinearLayout.VERTICAL);
        page.addView(savedClipsLayout);

        // Add dummy/initial sample clip
        addClipItem("Sample Clip #1 - Highlights", "Vertical 9:16 • 30s • Viral Captions", "00:01:15 - 00:01:45");

        TextView note = new TextView(this);
        note.setTextColor(Color.rgb(148, 163, 184));
        note.setTextSize(12);
        note.setPadding(0, 24, 0, 0);
        note.setText("Note: AI processing is optimized for vertical platforms. Videos uploaded or linked will be processed on backend service for speech transcription, highlight detection, and 9:16 rendering.");
        page.addView(note);

        create.setOnClickListener(v -> processCreateShorts());

        setContentView(scroll);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedVideoUri = data.getData();
            selectedFileText.setText("📁 Selected: " + selectedVideoUri.getLastPathSegment());
            selectedFileText.setTextColor(Color.rgb(52, 211, 153)); // Light green
        }
    }

    private void processCreateShorts() {
        String yt = youtubeUrl.getText().toString().trim();
        String gd = driveUrl.getText().toString().trim();

        if (selectedVideoUri == null && yt.isEmpty() && gd.isEmpty()) {
            status.setText("⚠️ Please select a local video file or enter a YouTube/Drive URL first.");
            status.setTextColor(Color.rgb(248, 113, 113));
            return;
        }

        if (!yt.isEmpty() && !yt.startsWith("https://") && !yt.startsWith("http://")) {
            status.setText("⚠️ Please enter a valid YouTube URL beginning with https://");
            status.setTextColor(Color.rgb(248, 113, 113));
            return;
        }

        if (!gd.isEmpty() && !gd.startsWith("https://") && !gd.startsWith("http://")) {
            status.setText("⚠️ Please enter a valid Google Drive URL beginning with https://");
            status.setTextColor(Color.rgb(248, 113, 113));
            return;
        }

        String platform = targetPlatform.getSelectedItem().toString();
        String dur = duration.getSelectedItem().toString();
        String style = captionStyle.getSelectedItem().toString();
        String ratio = aspectRatio.getSelectedItem().toString();

        String sourceName = selectedVideoUri != null ? "Local File: " + selectedVideoUri.getLastPathSegment() : (!yt.isEmpty() ? "YouTube URL" : "Drive URL");

        status.setTextColor(Color.rgb(52, 211, 153));
        status.setText("⏳ AI Processing Started...\n" +
                "• Source: " + sourceName + "\n" +
                "• Target: " + platform + "\n" +
                "• Duration: " + dur + "\n" +
                "• Format: " + ratio + "\n" +
                "• Caption Style: " + style + "\n\n" +
                "AI is detecting key highlights, re-framing video to 9:16, generating subtitles, and cutting short clips.");

        // Add new generated clip to Saved Clips
        String clipTitle = "Short Clip - " + platform.replace("📱 ", "").replace("📸 ", "").replace("🎵 ", "");
        String clipDetails = ratio + " • " + dur + " • " + style;
        addClipItem(clipTitle, clipDetails, "Just now");
    }

    private TextView sectionHeader(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.rgb(248, 250, 252));
        t.setTextSize(17);
        t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, 20, 0, 10);
        return t;
    }

    private EditText input(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextColor(Color.WHITE);
        e.setHintTextColor(Color.rgb(148, 163, 184));
        e.setSingleLine(false);
        e.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        e.setPadding(20, 16, 20, 16);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(12);
        shape.setColor(Color.rgb(30, 41, 59));
        e.setBackground(shape);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 8, 0, 16);
        e.setLayoutParams(lp);
        return e;
    }

    private TextView label(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.rgb(226, 232, 240));
        t.setTextSize(14);
        t.setPadding(0, 10, 0, 6);
        return t;
    }

    private Spinner createSpinner(String[] options) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(adapter);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(12);
        shape.setColor(Color.rgb(30, 41, 59));
        spinner.setBackground(shape);
        spinner.setPadding(16, 16, 16, 16);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 4, 0, 16);
        spinner.setLayoutParams(lp);
        return spinner;
    }

    private CheckBox check(String text) {
        CheckBox c = new CheckBox(this);
        c.setText(text);
        c.setTextColor(Color.rgb(226, 232, 240));
        c.setPadding(8, 8, 8, 8);
        return c;
    }

    private Button createStyledButton(String text, int color) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setTypeface(null, Typeface.BOLD);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(16);
        shape.setColor(color);
        b.setBackground(shape);
        b.setPadding(20, 20, 20, 20);
        return b;
    }

    private void addClipItem(String titleStr, String subtitleStr, String timestamp) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(20, 16, 20, 16);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(12);
        shape.setColor(Color.rgb(30, 41, 59));
        shape.setStroke(2, Color.rgb(51, 65, 85));
        item.setBackground(shape);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 8, 0, 8);
        item.setLayoutParams(lp);

        TextView clipTitle = new TextView(this);
        clipTitle.setText("🎥 " + titleStr);
        clipTitle.setTextColor(Color.WHITE);
        clipTitle.setTextSize(15);
        clipTitle.setTypeface(null, Typeface.BOLD);
        item.addView(clipTitle);

        TextView clipDesc = new TextView(this);
        clipDesc.setText(subtitleStr + " (" + timestamp + ")");
        clipDesc.setTextColor(Color.rgb(148, 163, 184));
        clipDesc.setTextSize(12);
        clipDesc.setPadding(0, 4, 0, 10);
        item.addView(clipDesc);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button saveBtn = createStyledButton("💾 Save Clip", Color.rgb(16, 185, 129));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, -2, 1);
        btnLp.setMargins(0, 0, 8, 0);
        saveBtn.setLayoutParams(btnLp);
        saveBtn.setTextSize(12);

        Button shareBtn = createStyledButton("📤 Export / Share", Color.rgb(99, 102, 241));
        LinearLayout.LayoutParams shareLp = new LinearLayout.LayoutParams(0, -2, 1);
        shareLp.setMargins(8, 0, 0, 0);
        shareBtn.setLayoutParams(shareLp);
        shareBtn.setTextSize(12);

        buttons.addView(saveBtn);
        buttons.addView(shareBtn);
        item.addView(buttons);

        saveBtn.setOnClickListener(v -> Toast.makeText(this, "Clip saved to device storage!", Toast.LENGTH_SHORT).show());
        shareBtn.setOnClickListener(v -> Toast.makeText(this, "Preparing clip for export...", Toast.LENGTH_SHORT).show());

        savedClipsLayout.addView(item, 0);
    }
}
