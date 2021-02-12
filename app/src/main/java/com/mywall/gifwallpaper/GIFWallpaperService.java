package com.mywall.gifwallpaper;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class GIFWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        try {
            Log.e("open", "gif");

            Movie movie = Movie.decodeStream(
                    getResources().openRawResource(R.raw.gif_wall));

            Log.e("open222", "gif");

            return new GIFWallpaperEngine(movie);
        } catch (Exception e) {
            Log.e("GIF", "Could not load asset");
            e.printStackTrace();
            return null;
        }
    }

    private class GIFWallpaperEngine extends Engine {
        private final int frameDuration = 2;

        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;
        float mScaleX;
        float mScaleY;

        public GIFWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };

        private void draw() {
            if (visible) {
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                // Adjust size and position so that
                // the image looks good on your screen
                canvas.scale(mScaleX, mScaleY);
                movie.draw(canvas, 0, 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mScaleX = width / (1f * movie.width());
            mScaleY = height / (1f * movie.height());
            draw();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }

    }
}