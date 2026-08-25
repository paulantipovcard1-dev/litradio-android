package link.litradio.app;

import android.content.ComponentName;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

@UnstableApi
  public class MainActivity extends AppCompatActivity {

    private MediaController controller;
        private ListenableFuture<MediaController> controllerFuture;

    private ImageButton playButton;
        private TextView statusText;
        private TextView trackTitle;
        private boolean isPlaying = false;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
                  super.onCreate(savedInstanceState);
                  setContentView(R.layout.activity_main);

            playButton = findViewById(R.id.playButton);
                  statusText = findViewById(R.id.statusText);
                  trackTitle = findViewById(R.id.trackTitle);

            playButton.setOnClickListener(v -> togglePlayback());
        }

    @Override
        protected void onStart() {
                  super.onStart();
                  SessionToken token = new SessionToken(this, new ComponentName(this, PlaybackService.class));
                  controllerFuture = new MediaController.Builder(this, token).buildAsync();
                  controllerFuture.addListener(() -> {
                                try {
                                                  controller = controllerFuture.get();
                                                  attachListener();
                                                  updatePlayButtonIcon();
                                } catch (Exception e) {
                                                  statusText.setText("Памылка падключэння");
                                }
                  }, MoreExecutors.directExecutor());
        }

    private void attachListener() {
              controller.addListener(new Player.Listener() {
                            @Override
                            public void onMediaMetadataChanged(@NonNull MediaMetadata mediaMetadata) {
                                              CharSequence title = mediaMetadata.title;
                                              if (title != null && title.length() > 0) {
                                                                    trackTitle.setText(title);
                                                                    statusText.setText("У эфіры");
                                              } else {
                                                                    statusText.setText("Не ўдалося атрымаць назву");
                                              }
                            }

                                                 @Override
                            public void onPlaybackStateChanged(int state) {
                                              if (state == Player.STATE_BUFFERING) {
                                                                    statusText.setText("Падключэнне...");
                                              }
                                              updatePlayButtonIcon();
                            }

                                                 @Override
                            public void onIsPlayingChanged(boolean playing) {
                                              isPlaying = playing;
                                              updatePlayButtonIcon();
                            }
              });
              isPlaying = controller.isPlaying();
    }

    private void togglePlayback() {
              if (controller == null) return;
              if (isPlaying) {
                            controller.pause();
              } else {
                            controller.play();
              }
    }

    private void updatePlayButtonIcon() {
              playButton.setImageResource(isPlaying
                                                          ? android.R.drawable.ic_media_pause
                                                          : android.R.drawable.ic_media_play);
    }

    @Override
        protected void onStop() {
                  if (controllerFuture != null) {
                                MediaController.releaseFuture(controllerFuture);
                  }
                  super.onStop();
        }
  }
