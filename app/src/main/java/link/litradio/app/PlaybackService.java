app/src/main/java/link/litradio/app/PlaybackService.javapackage link.litradio.app;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class PlaybackService extends MediaSessionService {

    private static final String STREAM_URL = "https://litradio.link/radio.mp3";

    private MediaSession mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        ExoPlayer player = new ExoPlayer.Builder(this).build();
        player.setMediaItem(MediaItem.fromUri(STREAM_URL));
        player.prepare();

        mediaSession = new MediaSession.Builder(this, player).build();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }
}
